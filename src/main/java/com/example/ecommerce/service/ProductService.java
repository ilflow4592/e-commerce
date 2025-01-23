package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface ProductService {
    Long createProduct(CreateProductDto createProductDto, MultipartFile file);
    PageableDto<ProductDto> searchProducts(String keyword, Category category, Size productSize, Pageable pageable, String entryPoint);
    PageableDto<ProductDto> getAllProducts(Pageable pageable);
    PageableDto<ProductDto> getShopDisplayableProducts(Pageable pageable);
    ProductDto getProduct(Long id);
    ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile file);
    void deleteProduct(Long id);
}
