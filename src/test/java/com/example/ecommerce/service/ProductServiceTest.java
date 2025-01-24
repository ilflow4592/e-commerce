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
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private S3Service s3Service;
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
                .fileName("file_name")
                .fileKey("file_key")
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

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                new byte[0]
        );

        String fakeFileKey = "generated-file-key";
        when(s3Service.uploadFile(file)).thenReturn(fakeFileKey);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Long productId = productService.createProduct(createProductDto, file);

        System.out.println("product.getId() = " + product.getId());
        System.out.println("productId = " + productId);

        // then
        assertEquals(product.getId(), productId);
        verify(s3Service, times(1)).uploadFile(file); // S3 업로드 검증
        verify(productRepository, times(1)).save(any(Product.class)); // DB 저장 검증
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

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                new byte[0]
        );

        String fakeFileKey = "generated-file-key";

        // when
        Product product = CreateProductDto.toEntity(createProductDto, file, fakeFileKey);

        // then
        assertThat(createProductDto.avgRating()).isEqualTo(null); //  DTO의 avgRating은 null이어야 함
        assertThat(product.getAvgRating()).isEqualTo(0.0f); //  엔티티에서는 0.0f로 설정되어야 함
        assertThat(product.getFileKey()).isEqualTo(fakeFileKey); //  fileKey가 정상적으로 설정되어야 함
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
        ProductDto dto = ProductDto.builder()
                .id(1L)
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .fileName("file_name")
                .fileUrl(null)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        ProductDto productDto = productService.getProduct(1L);

        System.out.println("productDto = " + productDto);

        // then
        assertNotNull(productDto);
        assertThat(productDto).usingRecursiveComparison().isEqualTo(dto);
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
    @DisplayName("상품을 갱신할 수 있다. (파일 없이)")
    void updateProductWithoutFile() {
        // given
        ProductDto productDto = ProductDto.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build();

        //모킹 객체
        Product product = spy(Product.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(1L, productDto, null);

        // then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(s3Service, never()).uploadFile(any()); // 파일 업로드가 호출되지 않아야 함
        verify(product, times(1)).update(productDto, null, null); // fileName과 fileKey가 null로 전달되어야 함
    }

    @Test
    @DisplayName("상품을 갱신할 수 있다. (파일 포함)")
    void updateProductWithFile() {
        // given
        ProductDto productDto = ProductDto.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build();

        //모킹 객체
        Product product = spy(Product.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build()
        );

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-image.png",
                "image/png",
                new byte[0]
        );

        String fakeFileKey = "updated-file-key";
        when(s3Service.uploadFile(file)).thenReturn(fakeFileKey);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(1L, productDto, file);

        // then
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).uploadFile(file); // 파일 업로드가 호출되어야 함
        verify(product, times(1)).update(productDto, "updated-image.png", fakeFileKey); // 파일명과 fileKey가 올바르게 전달됨
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
