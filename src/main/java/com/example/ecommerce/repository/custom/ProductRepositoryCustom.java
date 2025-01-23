package com.example.ecommerce.repository.custom;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.entity.Product;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    PageableDto<Product> searchProducts(String keyword, Category category, Size productSize, Pageable pageable, String entryPoint);
}