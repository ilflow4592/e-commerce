package com.example.ecommerce.controller;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.repository.custom.ProductRepositoryCustom;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductRepositoryCustom productRepositoryCustom;

    @PostMapping
    public ResponseEntity<?> createProduct(
            @Valid @RequestPart("createProductDto") CreateProductDto createProductDto,
            @RequestPart("file") MultipartFile file
    ){
        log.info("ProductController - createProduct(POST) : " + createProductDto);

        ResponseEntity<String> body = checkFileValidation(file);
        if (body != null) return body;

        Long productId = productService.createProduct(createProductDto, file);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }
    @GetMapping("/search")
    public ResponseEntity<PageableDto<ProductDto>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) Size productSize,
            Pageable pageable
    ){
        PageableDto<ProductDto> productDtoPageableDto = productRepositoryCustom.searchProducts(keyword, category, productSize, pageable);
        return new ResponseEntity<>(productDtoPageableDto,HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PageableDto<ProductDto>> getAllProducts(Pageable pageable){
        PageableDto<ProductDto> pageableProductDto = productService.getAllProducts(pageable);
        return new ResponseEntity<>(pageableProductDto, HttpStatus.OK);
    }
    @GetMapping("/shop")
    public ResponseEntity<PageableDto<ProductDto>> getShopDisplayableProducts(Pageable pageable){
        PageableDto<ProductDto> shopDisplayableProducts = productService.getShopDisplayableProducts(pageable);
        return new ResponseEntity<>(shopDisplayableProducts, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        ProductDto dto = productService.getProduct(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart(value = "productDto") ProductDto productDto,
            @RequestPart(value = "file", required = false) MultipartFile file
            ){
        log.info("ProductController - updateProduct(PATCH) : " + productDto);
        log.info("file" + file);

        ProductDto dto = productService.updateProduct(id, productDto, file);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok("id = " + id + "인 상품이 성공적으로 삭제되었습니다.");
    }

    private static ResponseEntity<String> checkFileValidation(MultipartFile file) {
        //파일이 존재하는지 체크
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        // MIME 타입이 image/png 인지 확인
        String contentType = file.getContentType();
        if (!"image/png".equalsIgnoreCase(contentType)) {
            return ResponseEntity.badRequest().body("PNG 파일만 업로드할 수 있습니다.");
        }
        return null;
    }

}
