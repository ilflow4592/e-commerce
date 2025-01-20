package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {
    Long createProduct(CreateProductDto createProductDto, MultipartFile file);
    PageableDto<ProductDto> getAllProducts(Pageable pageable);
    List<ProductDto> getShopDisplayableProducts();
    ProductDto getProduct(Long id);
    ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile file);
    void deleteProduct(Long id);
}
