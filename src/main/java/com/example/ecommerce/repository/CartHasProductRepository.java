package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CartHasProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartHasProductRepository extends JpaRepository<CartHasProduct, Long> {
}