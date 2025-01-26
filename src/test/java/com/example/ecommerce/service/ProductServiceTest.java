package com.example.ecommerce.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
import com.example.ecommerce.repository.custom.ProductRepositoryCustom;
import java.util.List;
import java.util.Optional;
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
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductRepositoryCustom productRepositoryCustom;
    @Mock
    private S3Service s3Service;
    @InjectMocks
    private ProductServiceImpl productService;
    private Product product;
    private Product product1;
    private Product product2;
    private List<ProductDto> productDtoList;
    private List<Product> productList;

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
            .shopDisplayable(true)
            .fileName("product_image.png")
            .fileKey("uuid-product_image.png")
            .avgRating(0.0f)
            .build();

        productList = List.of(Product.builder()
                .id(2L)
                .name("패딩 점퍼")
                .description("따뜻한 겨울 점퍼")
                .unitPrice(120000)
                .stockQuantity(30)
                .category(Category.OUTER)
                .size(Size.L)
                .shopDisplayable(true)
                .fileName("padding.png")
                .fileKey("file-key-1")
                .build(),
            Product.builder()
                .id(3L)
                .name("트렌치 코트")
                .description("세련된 스타일의 코트")
                .unitPrice(150000)
                .stockQuantity(20)
                .category(Category.OUTER)
                .size(Size.M)
                .shopDisplayable(false)
                .fileName("coat.png")
                .fileKey("file-key-2")
                .build()
        );

        product1 = productList.get(0);
        product2 = productList.get(1);

        // ProductDto 변환 리스트 생성
        productDtoList = List.of(
            ProductDto.builder()
                .id(product1.getId())
                .name(product1.getName())
                .description(product1.getDescription())
                .unitPrice(product1.getUnitPrice())
                .stockQuantity(product1.getStockQuantity())
                .category(product1.getCategory().name())
                .size(product1.getSize().name())
                .shopDisplayable(product1.getShopDisplayable())
                .fileName(product1.getFileName())
                .fileUrl("https://s3.com/" + product1.getFileKey())
                .build(),

            ProductDto.builder()
                .id(product2.getId())
                .name(product2.getName())
                .description(product2.getDescription())
                .unitPrice(product2.getUnitPrice())
                .stockQuantity(product2.getStockQuantity())
                .category(product2.getCategory().name())
                .size(product2.getSize().name())
                .shopDisplayable(product2.getShopDisplayable())
                .fileName(product2.getFileName())
                .fileUrl("https://s3.com/" + product2.getFileKey())
                .build()
        );
    }

    // request : CreateProductDto createProductDto, MultipartFile file
    // response : Long id
    @Test
    @DisplayName("상품 생성")
    void createProduct() {
        // given
        CreateProductDto createProductDto = CreateProductDto.builder()
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.PANTS))
            .size(String.valueOf(Size.M))
            .shopDisplayable(true)
            .build();

        MultipartFile file = new MockMultipartFile(
            "file",
            "product_image.png",
            "image/png",
            new byte[0]
        );

        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(
            "file-key");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Long productId = productService.createProduct(createProductDto, file);

        // then
        verify(s3Service, times(1)).uploadFile(file);
        verify(productRepository, times(1)).save(any(Product.class));

        assertNotNull(productId);
        assertEquals(product.getId(), productId);
    }

    @Test
    @DisplayName("상품 생성 - createProducDto.toEntity() 변환 확인")
    void createProduct_createProductDto_toEntity_check() {
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
            "product_image.png",
            "image/png",
            new byte[0]
        );

        String fileKey = "uuid-product_image.png";

        // when
        Product convertedProduct = createProductDto.toEntity(createProductDto, file,
            fileKey);

        // then
        verify(createProductDto, times(1)).toEntity(createProductDto, file, fileKey);

        assertThat(convertedProduct).usingRecursiveComparison().ignoringFields("id")
            .isEqualTo(product);
    }

    // request : Pageable pageable
    // response : PageableDto<ProductDto> returnedPageableDto
    @Test
    @DisplayName("상품 전체 조회")
    void getAllProducts() {
        // given
        List<Product> productList = IntStream.range(0, 2)
            .mapToObj(i -> Product.builder()
                .id((long) i)
                .name("치노 팬츠 " + i)
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .shopDisplayable(true)
                .fileName("file_name" + "_" + i + ".png")
                .fileKey("uuid-file_name" + "_" + i + ".png")
                .build())
            .toList();

        Pageable pageable = PageRequest.of(1, 10, Sort.by("name").ascending());
        Page<Product> mockPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(mockPage);

        // when
        PageableDto<ProductDto> result = productService.getAllProducts(pageable);

        // then
        verify(productRepository, times(1)).findAll(pageable); // findAll이 한 번 호출되었는지 확인

        assertNotNull(result);
        assertEquals(result.page() - 1, 1);
        assertEquals(result.size(), 10);
        assertEquals(result.data().size(), 2);
        assertThat(result.data().get(0).name()).isEqualTo("치노 팬츠 0");
        assertThat(result.data().get(1).name()).isEqualTo("치노 팬츠 1");
    }

    // request : String keyword, Category category, Size productSize, Pageable pageable, String entryPoint
    // response : PageableDto<ProductDto>
    @Test
    @DisplayName("상품 검색 - entryPoint 미포함(shopDisplayable : true, false 둘 다 가능)")
    void searchProductsWithoutEntryPoint() {
        // given
        String keyword = "패딩";
        Category category = Category.OUTER;
        Size size = Size.L;

        Pageable pageable = PageRequest.of(1, 10, Sort.by("name").ascending());
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(productList, false, 1, 10);

        when(productRepositoryCustom.searchProducts(keyword, category, size, pageable, null))
            .thenReturn(mockProductPageableDto);

        // when
        when(s3Service.getPresignedUrl("file-key-1")).thenReturn("https://s3.com/file-key-1");
        when(s3Service.getPresignedUrl("file-key-2")).thenReturn("https://s3.com/file-key-2");
        PageableDto<ProductDto> result = productService.searchProducts(keyword, category, size,
            pageable, null);

        System.out.println("result = " + result);

        // then
        verify(productRepositoryCustom, times(1)).searchProducts(keyword, category, size, pageable,
            null);
        verify(s3Service, times(1)).getPresignedUrl("file-key-1");
        verify(s3Service, times(1)).getPresignedUrl("file-key-2");

        assertEquals(result.data().size(), 2);
        assertThat(result.last()).isFalse();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);

        assertThat(result.data().get(0))
            .usingRecursiveComparison()
            .isEqualTo(productDtoList.get(0));
        assertThat(result.data().get(1))
            .usingRecursiveComparison()
            .isEqualTo(productDtoList.get(1));
    }

    // request : String keyword, Category category, Size productSize, Pageable pageable, String entryPoint
    // response : PageableDto<ProductDto>
    @Test
    @DisplayName("상품 검색 - entryPoint 포함(shop)")
    void searchProductsWithEntryPoint() {
        // given
        // shopDisplayable = true인 상품 리스트 (entryPoint = "shop"일 때 필터링되어야 함)
        List<Product> shopDisplayableProducts = List.of(product1);

        // ProductDto 변환 리스트 (shopDisplayable = true만 포함)
        List<ProductDto> shopDisplayableProductDtos = List.of(
            productDtoList.get(0)

        );
        String keyword = "패딩";
        Category category = Category.OUTER;
        Size size = Size.L;
        String entryPoint = "shop";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(shopDisplayableProducts,
            false, 1, 10);

        // Mock 설정: shopDisplayable = true인 상품만 반환해야 함
        when(productRepositoryCustom.searchProducts(keyword, category, size, pageable, entryPoint))
            .thenReturn(mockProductPageableDto);
        when(s3Service.getPresignedUrl("file-key-1")).thenReturn("https://s3.com/file-key-1");

        // when
        PageableDto<ProductDto> result = productService.searchProducts(keyword, category, size,
            pageable, entryPoint);

        // then
        verify(productRepositoryCustom, times(1)).searchProducts(keyword, category, size, pageable,
            entryPoint);
        verify(s3Service, times(1)).getPresignedUrl("file-key-1");
        verify(s3Service, never()).getPresignedUrl(
            "file-key-2");

        assertEquals(result.data().size(), 1);
        assertThat(result.last()).isFalse();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);

        assertThat(result.data().get(0).shopDisplayable()).isTrue();
        assertThat(result.data().get(0))
            .usingRecursiveComparison()
            .isEqualTo(shopDisplayableProductDtos.get(0));

    }

    // request : List<Product> productDtoPageableDto
    // response : List<ProductDto>
    @Test
    @DisplayName("Product 리스트를 ProductDto 리스트로 변환 - S3 Presigned URL 포함")
    void convertToProductDtoListTest() {
        //given
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(productList,
            true, 1, 10);

        when(s3Service.getPresignedUrl("file-key-1")).thenReturn("https://s3.com/file-key-1");
        when(s3Service.getPresignedUrl("file-key-2")).thenReturn("https://s3.com/file-key-2");

        // when
        List<ProductDto> result = productService.convertToProductDtoList(
            mockProductPageableDto.data());

        // then
        verify(s3Service, times(1)).getPresignedUrl("file-key-1");
        verify(s3Service, times(1)).getPresignedUrl("file-key-2");

        assertEquals(result.size(), 2);

        assertThat(result.get(0)).usingRecursiveComparison()
            .isEqualTo(productDtoList.get(0));
        assertThat(result.get(1)).usingRecursiveComparison()
            .isEqualTo(productDtoList.get(1));
    }

    // request : Long id
    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("상품 단일 조회")
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
            .fileName("product_image.png")
            .fileUrl("file_url")
            .build();

        String fileUrl = "file_url";

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(s3Service.getPresignedUrl(product.getFileKey())).thenReturn(fileUrl);

        // when
        ProductDto productDto = productService.getProduct(1L);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(s3Service, times(1)).getPresignedUrl(product.getFileKey());

        assertNotNull(productDto);
        assertThat(productDto).usingRecursiveComparison().isEqualTo(returnedProductDto);
    }

    @Test
    @DisplayName("찾는 상품이 존재하지 않을 시, ProductNotFoundException 예외를 던진다.")
    void getProduct_ProductNotFoundException() {
        // given
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }


    // request : Long id, ProductDto productDto, MultipartFile file
    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("상품 갱신 - 파일 포함")
    void updateProductWithFile() {
        // given
        Long id = 1L;

        ProductDto productDto = ProductDto.builder()
            .id(1L)
            .name("패딩 점퍼")
            .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.OUTER))
            .size(String.valueOf(Size.L))
            .shopDisplayable(true)
            .fileName("product_image.png")
            .fileUrl("file_url")
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "product_image.png",
            "image/png",
            new byte[0]
        );

        String fileKey = "file-key";

        when(s3Service.uploadFile(file)).thenReturn(fileKey);
        when(s3Service.getPresignedUrl(fileKey)).thenReturn("file_url");
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(id, productDto, file);

        // then
        verify(s3Service, times(1)).uploadFile(file);
        verify(productRepository, times(1)).findById(id);

        assertThat(result).usingRecursiveComparison().isEqualTo(productDto);
    }

    @Test
    @DisplayName("상품 갱신 - 파일 미포함")
    void updateProductWithoutFile() {
        // given
        Long id = 1L;

        ProductDto productDto = ProductDto.builder()
            .id(1L)
            .name("패딩 점퍼")
            .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.OUTER))
            .size(String.valueOf(Size.L))
            .shopDisplayable(true)
            .fileName("product_image.png")
            .fileUrl(null)
            .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        ProductDto result = productService.updateProduct(id, productDto, null);

        // then
        verify(productRepository, times(1)).findById(id);

        assertThat(result).usingRecursiveComparison().isEqualTo(productDto);
    }

    // request : Long id
    // response : void
    @Test
    @DisplayName("상품 제거")
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
