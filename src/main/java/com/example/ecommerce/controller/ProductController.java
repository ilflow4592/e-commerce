package com.example.ecommerce.controller;

import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.repository.custom.ProductRepositoryCustom;
import com.example.ecommerce.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepositoryCustom productRepositoryCustom;

    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody CreateProductDto productDto){
        Long productId = productService.createProduct(productDto);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<PageableDto<ProductDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ){
        PageableDto<ProductDto> productDtoPageableDto = productRepositoryCustom.searchProducts(keyword, pageable);
        return new ResponseEntity<>(productDtoPageableDto,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableDto<ProductDto>> getAllProducts(Pageable pageable){
        PageableDto<ProductDto> pageableProductDto = productService.getAllProducts(pageable);
        return new ResponseEntity<>(pageableProductDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        ProductDto dto = productService.getProduct(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto){
        ProductDto dto = productService.updateProduct(id, productDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok("id = " + id + "인 상품이 성공적으로 삭제되었습니다.");
    }

}
