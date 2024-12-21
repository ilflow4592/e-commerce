package com.example.ecommerce.repository.custom;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    PageableDto<ProductDto> searchProducts(String keyword, Pageable pageable);
}