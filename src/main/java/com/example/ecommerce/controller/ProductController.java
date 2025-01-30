package com.example.ecommerce.controller;

import com.example.ecommerce.common.aop.ControllerLog;
import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@ControllerLog
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
        @Valid @RequestPart("createProductDto") CreateProductDto createProductDto,
        @RequestPart("file") MultipartFile file
    ) {
        ProductDto productDto = productService.createProduct(createProductDto, file);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<PageableDto<ProductDto>> searchProducts(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Size productSize,
        @RequestParam(required = false) String entryPoint,
        Pageable pageable
    ) {
        PageableDto<ProductDto> productDtoPageableDto = productService.searchProducts(keyword,
            category, productSize, pageable, entryPoint);
        return new ResponseEntity<>(productDtoPageableDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableDto<ProductDto>> getAllProducts(Pageable pageable) {
        PageableDto<ProductDto> pageableProductDto = productService.getAllProducts(pageable);
        return new ResponseEntity<>(pageableProductDto, HttpStatus.OK);
    }

    @GetMapping("/shop")
    public ResponseEntity<PageableDto<ProductDto>> getShopDisplayableProducts(Pageable pageable) {
        PageableDto<ProductDto> shopDisplayableProducts = productService.getShopDisplayableProducts(
            pageable);
        return new ResponseEntity<>(shopDisplayableProducts, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        ProductDto productDto = productService.getProduct(id);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDto> updateProduct(
        @PathVariable Long id,
        @Valid @RequestPart(value = "productDto") ProductDto productDto,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        ProductDto dto = productService.updateProduct(id, productDto, file);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("id = " + id + "인 상품이 성공적으로 삭제되었습니다.");
    }

}
