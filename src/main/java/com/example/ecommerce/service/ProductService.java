package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    Long createProduct(CreateProductDto createProductDto);
    PageableDto<ProductDto> getAllProducts(Pageable pageable);
    ProductDto getProduct(Long id);
    ProductDto updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
}
