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

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .build();
    }

    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void createProduct() {
        // given
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Long productId = productService.createProduct(createProductDto);

        // then
        assertEquals(product.getId(), productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("createProductDto에서 엔티티로 변환할 때, avgRating 필드는 0.0f로 설정되어야 한다.")
    void createProduct_setDefaultAvgRatingToZero() {
        // given
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .build();

        // when
        Product product = CreateProductDto.toEntity(createProductDto);

        // then
        assertThat(createProductDto.avgRating()).isEqualTo(null); // dto에서 avgRating을 null로
        assertThat(product.getAvgRating()).isEqualTo(0.0f); // 엔티티에서는 0.0f로 세팅
    }

    @Test
    @DisplayName("존재하는 모든 상품을 조회할 수 있다.")
    void getAllProducts() {
        // given
        Page<Product> productsPage = new PageImpl<>(Collections.singletonList(product));
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(productsPage);

        // when
        PageableDto<ProductDto> result = productService.getAllProducts(pageable);

        System.out.println("result = " + result);
        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.page());;
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("단일 상품을 조회할 수 있다.")
    void getProduct() {
        // given
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        ProductDto productDto = productService.getProduct(1L);

        // then
        assertNotNull(productDto);
        assertThat(productDto).usingRecursiveComparison().isEqualTo(product);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("찾는 상품이 존재하지 않을 시, ProductNotFoundException 예외를 던진다.")
    void getProduct_NotFound() {
        // given
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("상품을 갱신할 수 있다.")
    void updateProduct() {
        // given
        ProductDto productDto = ProductDto.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(1L, productDto);

        // then
        assertNotNull(result);
        assertThat(result).usingRecursiveComparison().isEqualTo(product);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("상품을 제거할 수 있다.")
    void deleteProduct() {
        // given
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(product.getId());

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}
