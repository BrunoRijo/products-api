package com.example.apiProducts.controllers;

import com.example.apiProducts.dtos.ProductRecordDto;
import com.example.apiProducts.models.ProductModel;
import com.example.apiProducts.repositories.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/", produces = {"application/json"})
@Tag(name = "Produtos")
//http://localhost:8080/swagger-ui/index.html#/
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    @Operation(summary = "Realiza o insert do Produto na base de dados.",description = "Insere um produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso!"),
            @ApiResponse(responseCode = "201", description = "Produto inserido com sucesso!"),
            @ApiResponse(responseCode = "400", description = "Erro ao inserir, verifique os parametros!"),
            @ApiResponse(responseCode = "415", description = "Tipo de dado não suportado, verifique os parametros!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @PostMapping(value = "/products",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto product){
        var productModel = new ProductModel();
        BeanUtils.copyProperties(product, productModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productRepository.save(productModel));
    }

    @Operation(
            summary = "Realiza a busca de todos os produtos na base de dados.",
            description = "Recupera a lista de todos os produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso!"),
            @ApiResponse(responseCode = "422", description = "Parâmetros inválidos!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){
        List<ProductModel> productsList = productRepository.findAll();
        if (!productsList.isEmpty()) {
            for (ProductModel product : productsList){
                //Adiciona uma linha em cada produto da lista, que redireciona para os detalhes do produto
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(product.getIdProduct())).withSelfRel());
            }
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productsList);
    }

    @Operation(
            summary = "Realiza a busca pelo id de um produto na base de dados.",
            description = "Recupera um produto pelo seu id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso!"),
            @ApiResponse(responseCode = "422", description = "Parâmetro inválido!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);

        if (product0.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }
        product0.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(product0.get());
    }

    @Operation(
            summary = "Realiza a atualização de um produto na base de dados.",
            description = "Atualiza um produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update realizado com sucesso!"),
            @ApiResponse(responseCode = "415", description = "Tipo de dado não suportado, verifique os parametros!"),
            @ApiResponse(responseCode = "422", description = "Parâmetros inválidos!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @PutMapping(value = "/products/{id}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto updatedProduct){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        var productModel = product0.get();
        BeanUtils.copyProperties(updatedProduct, productModel);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productRepository.save(productModel));
    }
    @Operation(
            summary = "Realiza a remoção de um produto na base de dados.",
            description = "Remove um produto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso!"),
            @ApiResponse(responseCode = "422", description = "Parâmetro inválido!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        }

        productRepository.delete(product0.get());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Product deleted sucessfully.");
    }

    @Operation(
            summary = "Realiza a busca de produtos que possuam o nome como parte seu titulo.",
            description = "Busca produtos por parte de seu nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso!"),
            @ApiResponse(responseCode = "422", description = "Parâmetro inválido!"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor!")
    })
    @GetMapping("/products/byName/")
    public ResponseEntity<List<ProductModel>> getProductsByName(@RequestParam(value = "name") String name){
        List<ProductModel> productlistByName = productRepository.getProductByName(name);

        if (!productlistByName.isEmpty()){
            for (ProductModel product : productlistByName){
                product.add(linkTo(methodOn(ProductController.class).getProductsByName(name)).withRel("Products List with similar name's products"));
            }
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productlistByName);
    }

}

