package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product("패딩 점퍼", "방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.", 50000, 10 , Category.OUTER, Size.L);
        product.setId(1L);

        productDto = new ProductDto(1L, "패딩 점퍼", "방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.", 50000, 100, Category.OUTER, Size.L);
    }

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void createProduct() {
        // given
        CreateProductDto createProductDto = new CreateProductDto(
                "패딩 점퍼",
                "방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.",
                50000,
                100,
                Category.OUTER,
                Size.L
        );
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Long productId = productService.createProduct(createProductDto);

        // then
        assertEquals(1L, productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("존재하는 모든 상품들을 조회할 수 있다.")
    void getAllProducts() {
        // given
        Page<Product> productsPage = new PageImpl<>(Arrays.asList(product));
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable)).thenReturn(productsPage);

        // when
        PageableDto<ProductDto> result = productService.getAllProducts(pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("단일 상품을 조회할 수 있다.")
    void getProduct() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.getProduct(1L);

        // then
        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("찾는 상품이 존재하지 않을 시, ProductNotFoundException 예외를 던진다.")
    void getProduct_NotFound() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("상품을 갱신할 수 있다.")
    void updateProduct() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(1L, productDto);

        // then
        assertNotNull(result);
        assertEquals(productDto.name(), result.name());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("상품을 제거할 수 있다.")
    void deleteProduct() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(1L);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}
