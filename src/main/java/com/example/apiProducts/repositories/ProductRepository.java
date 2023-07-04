package com.example.apiProducts.repositories;

import com.example.apiProducts.models.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

    @Query("select p from ProductModel p where p.name like %:name% ")
    List<ProductModel> getProductByName(@Param("name") String name);
}
