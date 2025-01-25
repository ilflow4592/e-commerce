package com.example.ecommerce.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
        product = spy(Product.builder()
            .id(1L)
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(Category.PANTS)
            .size(Size.M)
            .shopDisplayable(true)
            .fileName("file_name")
            .fileKey("file_key")
            .build());
    }

    // request : CreateProductDto createProductDto, MultipartFile file
    // response : Long id
    @Test
    @DisplayName("상품을 생성할 수 있다.")
    void createProduct() {
        // given
        CreateProductDto createProductDto = spy(CreateProductDto.builder()
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.PANTS))
            .size(String.valueOf(Size.M))
            .shopDisplayable(true)
            .build());

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.png",
            "image/png",
            new byte[0]
        );

        String fileKey = "generated-file-key";

        when(s3Service.uploadFile(file)).thenReturn(fileKey);
        when(createProductDto.toEntity(createProductDto, file, fileKey)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Long productId = productService.createProduct(createProductDto, file);

        // then
        verify(s3Service, times(1)).uploadFile(file); // S3 업로드 검증
        verify(productRepository, times(1)).save(any(Product.class)); // DB 저장 검증
        verify(createProductDto, times(1)).toEntity(createProductDto, file, fileKey);

        assertNotNull(productId);
        assertEquals(product.getId(), productId);
    }

    // request : CreateProductDto createProductDto, MultipartFile file
    @Test
    @DisplayName("createProductDto에서 엔티티로 변환할 때, avgRating 필드는 0.0f로 설정되어야 한다.")
    void createProduct_setDefaultAvgRatingToZero() {
        // given
        CreateProductDto createProductDto = spy(CreateProductDto.builder()
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.PANTS))
            .size(String.valueOf(Size.M))
            .shopDisplayable(true)
            .build());

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.png",
            "image/png",
            new byte[0]
        );

        String fileKey = "generated-file-key";

        // when
        Product product = createProductDto.toEntity(createProductDto, file, fileKey);

        // then
        assertThat(createProductDto.avgRating()).isEqualTo(null); //  DTO의 avgRating은 null이어야 함
        assertThat(product.getAvgRating()).isEqualTo(0.0f); //  엔티티에서는 0.0f로 설정되어야 함
        assertThat(product.getFileKey()).isEqualTo(fileKey); //  fileKey가 정상적으로 설정되어야 함
        verify(createProductDto, times(1)).toEntity(createProductDto, file, fileKey);
    }

    // request : Pageable pageable
    // response : PageableDto<ProductDto> returnedPageableDto
    @Test
    @DisplayName("존재하는 모든 상품을 조회할 수 있다.")
    void getAllProducts() {
        // given
        List<Product> productList = IntStream.range(0, 10)
            .mapToObj(i -> Product.builder()
                .id((long) i)
                .name("치노 팬츠 " + i)
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .shopDisplayable(true)
                .fileName(null)
                .fileKey(null)
                .build())
            .collect(Collectors.toList());

        Page<Product> productsPage = new PageImpl<>(productList);
        Pageable pageable = PageRequest.of(1, 10);

        PageableDto<ProductDto> returnedPageableDto = spy(PageableDto.<ProductDto>builder()
            .data(productList.stream()
                .map(p -> ProductDto.builder()
                    .id(p.getId())
                    .name(p.getName())
                    .description(p.getDescription())
                    .unitPrice(p.getUnitPrice())
                    .stockQuantity(p.getStockQuantity())
                    .category(p.getCategory().name())
                    .size(p.getSize().name())
                    .fileName(p.getFileName())
                    .shopDisplayable(p.getShopDisplayable())
                    .fileUrl(null)
                    .build())
                .collect(Collectors.toList()))
            .page(1)
            .size(10)
            .last(true)
            .build());

        when(productRepository.findAll(pageable)).thenReturn(productsPage);

        // when
        PageableDto<ProductDto> result = productService.getAllProducts(pageable);

        // then
        verify(productRepository, times(1)).findAll(pageable);

        assertNotNull(result);
        assertEquals(result, returnedPageableDto);
        assertEquals(result.size(), 10);
        assertEquals(result.page(), 1);
    }


    // request : Long id
    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("단일 상품을 조회할 수 있다.")
    void getProduct() {
        // given
        Long id = 1L;

        ProductDto returnedProductDto = ProductDto.builder()
            .id(1L)
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.PANTS))
            .size(String.valueOf(Size.M))
            .shopDisplayable(true)
            .fileName("file_name")
            .fileUrl("file_url")
            .build();

        String fileUrl = "file_url";

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(product.getFileKey()).thenReturn("file_key");
        when(s3Service.getPresignedUrl("file_key")).thenReturn(fileUrl);
        when(product.getFileName()).thenReturn("file_name");

        // when
        ProductDto productDto = productService.getProduct(1L);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(product, times(1)).getFileKey();
        verify(s3Service, times(1)).getPresignedUrl("file_key");
        verify(product, times(1)).getFileName();

        assertNotNull(productDto);
        assertThat(productDto).usingRecursiveComparison().isEqualTo(returnedProductDto);
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


    // request : Long id, ProductDto productDto, MultipartFile file
    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("상품을 갱신할 수 있다.")
    void updateProductWithFile() {
        // given
        Long id = 1L;

        ProductDto productDto = ProductDto.builder()
            .name("패딩 점퍼")
            .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.OUTER))
            .size(String.valueOf(Size.L))
            .shopDisplayable(true)
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "updated-image.png",
            "image/png",
            new byte[0]
        );

        ProductDto returnedProducDto = ProductDto.builder()
            .id(1L)
            .name("패딩 점퍼")
            .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.OUTER))
            .size(String.valueOf(Size.L))
            .shopDisplayable(true)
            .fileName(null)
            .fileUrl(null)
            .build();

        String fileKey = "file-key";
        when(s3Service.uploadFile(file)).thenReturn(fileKey);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(id, productDto, file);

        // then
        verify(s3Service, times(1)).uploadFile(file); // 파일 업로드가 호출되어야 함
        verify(productRepository, times(1)).findById(1L);
        verify(product, times(1)).update(productDto, file.getOriginalFilename(),
            fileKey); // 파일명과 fileKey가 올바르게 전달됨

        assertNotNull(result);
        assertEquals(result, returnedProducDto);

    }

    // request : Long id
    // response : void
    @Test
    @DisplayName("상품을 제거할 수 있다.")
    void deleteProduct() {
        // given
        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(id);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}
