package com.example.apiProducts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API de Produtos",
                                version = "1.0",
								contact = @Contact(
										name = "Bruno Rijo",
										email = "jbrunorijo@gmail.com",
										url = "https://www.linkedin.com/in/brunorijo"
								),
								license = @License(
										name = "Licença - BrunoRijo",
										url = "https://github.com/BrunoRijo/products-api"
								),
								termsOfService = "Termos de serviço",
                                description = "API RESTful de Produtos, implementada por Bruno Rijo, utilizando Java 17 e SpringBoot 3, documentada com Swagger 3.0.0"))
public class ApiDeProdutosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDeProdutosApplication.class, args);
	}

}
