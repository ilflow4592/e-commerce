package com.example.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
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
    private final String S3_URL = "https://s3.com/";

    // request : CreateProductDto createProductDto, MultipartFile file
    // response : Long id
    @Test
    @DisplayName("상품 생성")
    void createProduct() {
        // given
        Product product = Product.builder()
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
            new byte[1] // 0으로 설정할 경우 empty file로 인식됨
        );

        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn(
            "file-key");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        ProductDto productDto = productService.createProduct(createProductDto, file);

        // then
        verify(s3Service, times(1)).uploadFile(file);
        verify(productRepository, times(1)).save(any(Product.class));

        assertNotNull(productDto);
        assertEquals(product.getId(), productDto.id());
    }

    @Test
    @DisplayName("상품 생성 - createProducDto.toEntity() 변환 확인")
    void createProduct_createProductDto_toEntity_check() {
        // given
        Product product = Product.builder()
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
            new byte[1]
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

    @Test
    @DisplayName("상품 생성 - 데이터베이스 저장")
    void createProduct_DB_save_check() {
        // given
        Product product = Product.builder()
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

        // when
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productRepository.save(product);

        // then
        verify(productRepository, times(1)).save(product);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(product);
    }

    @Test
    @DisplayName("상품 생성 - Product.toDto 변환 확인")
    void createProduct_Product_toDto_check() {
        // given
        Product product = Product.builder()
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

        ProductDto convertedProductDto = ProductDto.builder()
            .id(1L)
            .name("치노 팬츠")
            .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
            .unitPrice(50000)
            .stockQuantity(100)
            .category(String.valueOf(Category.PANTS))
            .size(String.valueOf(Size.M))
            .shopDisplayable(true)
            .fileName(null)
            .fileUrl(null)
            .build();

        ProductDto productDto;

        // when
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productRepository.save(product);

        productDto = Product.toDto(result);

        // then
        verify(productRepository, times(1)).save(product);

        assertThat(productDto)
            .usingRecursiveComparison()
            .isEqualTo(convertedProductDto);
    }

    // request : Pageable pageable
    // response : PageableDto<ProductDto> returnedPageableDto
    @Test
    @DisplayName("상품 전체 조회")
    void getAllProducts() {
        // given
        List<Product> productList = List.of(Product.builder()
                .id(1L)
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
                .id(2L)
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

        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(1, 10);
        Page<Product> mockPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(mockPage);

        // when
        PageableDto<ProductDto> result = productService.getAllProducts(pageable);

        // then
        verify(productRepository, times(1)).findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.page() - 1);
        assertEquals(10, result.size());
        assertEquals(2, result.data().size());
        assertThat(result.data().get(0).name()).isEqualTo("패딩 점퍼");
        assertThat(result.data().get(1).name()).isEqualTo("트렌치 코트");
    }

    // request : String keyword, Category category, Size productSize, Pageable pageable, String entryPoint
    // response : PageableDto<ProductDto>
    @Test
    @DisplayName("상품 검색 - entryPoint 미포함(shopDisplayable : true, false 둘 다 가능)")
    void searchProductsWithoutEntryPoint() {
        // given

        List<Product> productList = List.of(Product.builder()
                .id(1L)
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
                .id(2L)
                .name("트렌치 코트")
                .description("세련된 스타일의 코트")
                .unitPrice(150000)
                .stockQuantity(20)
                .category(Category.OUTER)
                .size(Size.L)
                .shopDisplayable(false)
                .fileName("coat.png")
                .fileKey("file-key-2")
                .build()
        );

        Product product1 = productList.get(0);
        Product product2 = productList.get(1);

        List<ProductDto> productDtoList = List.of(
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
                .fileUrl(S3_URL + product1.getFileKey())
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
                .fileUrl(S3_URL + product2.getFileKey())
                .build()
        );

        // 요청 파리미터
        String keyword = null;
        Category category = Category.OUTER;
        Size size = Size.L;
        String entryPoint = null;

        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(1, 10);
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(productList, true, 1, 10);

        when(productRepositoryCustom.searchProducts(keyword, category, size, pageable, entryPoint))
            .thenReturn(mockProductPageableDto);

        // when
        when(s3Service.getPresignedUrl(productList.get(0).getFileKey())).thenReturn(
            S3_URL + productList.get(0).getFileKey());
        when(s3Service.getPresignedUrl(productList.get(1).getFileKey())).thenReturn(
            S3_URL + productList.get(1).getFileKey());

        PageableDto<ProductDto> productDtoPageableDto = productService.searchProducts(keyword,
            category, size,
            pageable, entryPoint);

        // then
        verify(productRepositoryCustom, times(1)).searchProducts(keyword, category, size, pageable,
            entryPoint);
        verify(s3Service, times(1)).getPresignedUrl(productList.get(0).getFileKey());
        verify(s3Service, times(1)).getPresignedUrl(productList.get(1).getFileKey());

        assertEquals(2, productDtoPageableDto.data().size());
        assertThat(productDtoPageableDto.last()).isTrue();
        assertThat(productDtoPageableDto.page()).isEqualTo(1);
        assertThat(productDtoPageableDto.size()).isEqualTo(10);

        assertThat(productDtoPageableDto.data().get(0))
            .usingRecursiveComparison()
            .isEqualTo(productDtoList.get(0));
        assertThat(productDtoPageableDto.data().get(1))
            .usingRecursiveComparison()
            .isEqualTo(productDtoList.get(1));

        reset(productRepositoryCustom);
    }

    // request : String keyword, Category category, Size productSize, Pageable pageable, String entryPoint
    // response : PageableDto<ProductDto>
    @Test
    @DisplayName("상품 검색 - entryPoint 포함(shop)")
    void searchProductsWithEntryPoint() {
        // given
        List<Product> productList = List.of(Product.builder()
                .id(1L)
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
                .id(2L)
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

        Product product1 = productList.get(0);
        Product product2 = productList.get(1);

        List<ProductDto> productDtoList = List.of(
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
                .fileUrl(S3_URL + product1.getFileKey())
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
                .fileUrl(S3_URL + product2.getFileKey())
                .build()
        );

        // ProductDto 변환 리스트 (shopDisplayable = true만 포함)
        List<Product> shopDisplayableProducts = List.of(productList.get(0));

        List<ProductDto> shopDisplayableProductDtos = List.of(
            productDtoList.get(0)
        );

        String keyword = "패딩";
        Category category = Category.OUTER;
        Size size = Size.L;
        String entryPoint = "shop";

        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(1, 10);
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(shopDisplayableProducts,
            true, 1, 10);

        // Mock 설정: shopDisplayable = true인 상품만 반환해야 함
        when(productRepositoryCustom.searchProducts(keyword, category, size, pageable, entryPoint))
            .thenReturn(mockProductPageableDto);
        when(s3Service.getPresignedUrl(productList.get(0).getFileKey())).thenReturn(
            productDtoList.get(0).fileUrl());

        // when
        PageableDto<ProductDto> result = productService.searchProducts(keyword, category, size,
            pageable, entryPoint);

        System.out.println("result = " + result);

        // then
        verify(productRepositoryCustom, times(1)).searchProducts(keyword, category, size, pageable,
            entryPoint);
        verify(s3Service, times(1)).getPresignedUrl(productList.get(0).getFileKey());

        assertEquals(1, result.data().size());
        assertThat(result.last()).isTrue();
        assertThat(result.page()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);

        assertThat(result.data().get(0).shopDisplayable()).isTrue();
        assertThat(result.data().get(0))
            .usingRecursiveComparison()
            .isEqualTo(shopDisplayableProductDtos.get(0));

        reset(productRepositoryCustom);
    }

    // request : List<Product> productDtoPageableDto
    // response : List<ProductDto>
    @Test
    @DisplayName("Product 리스트를 ProductDto 리스트로 변환 - S3 Presigned URL 포함")
    void convertToProductDtoListTest() {
        //given
        List<Product> productList = List.of(Product.builder()
                .id(1L)
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
                .id(2L)
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

        Product product1 = productList.get(0);
        Product product2 = productList.get(1);

        List<ProductDto> productDtoList = List.of(
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
                .fileUrl(S3_URL + product1.getFileKey())
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
                .fileUrl(S3_URL + product2.getFileKey())
                .build()
        );

        // 페이지네이션 설정
        PageableDto<Product> mockProductPageableDto = new PageableDto<>(productList,
            false, 1, 10);

        when(s3Service.getPresignedUrl(productList.get(0).getFileKey())).thenReturn(
            S3_URL + productList.get(0).getFileKey());
        when(s3Service.getPresignedUrl(productList.get(1).getFileKey())).thenReturn(
            S3_URL + productList.get(1).getFileKey());

        // when
        List<ProductDto> result = productService.convertToProductDtoList(
            mockProductPageableDto.data());

        // then
        verify(s3Service, times(1)).getPresignedUrl(productList.get(0).getFileKey());
        verify(s3Service, times(1)).getPresignedUrl(productList.get(1).getFileKey());

        assertEquals(2, result.size());

        assertThat(result.get(0)).usingRecursiveComparison()
            .isEqualTo(productDtoList.get(0));
        assertThat(result.get(1)).usingRecursiveComparison()
            .isEqualTo(productDtoList.get(1));
    }

    //
//    // request : Long id
//    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("상품 단일 조회")
    void getProduct() {
        // given
        Long id = 1L;

        Product product = Product.builder()
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
            .fileUrl(S3_URL + product.getFileKey())
            .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(s3Service.getPresignedUrl(product.getFileKey())).thenReturn(
            returnedProductDto.fileUrl());

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
        Product product = Product.builder()
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

        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // when, then
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    //
//
//    // request : Long id, ProductDto productDto, MultipartFile file
//    // response : ProductDto returnedProductDto
    @Test
    @DisplayName("상품 갱신 - 파일 포함")
    void updateProductWithFile() {
        // given
        Long id = 1L;

        Product product = Product.builder()
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
            .fileUrl(S3_URL + product.getFileKey())
            .build();

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "product_image.png",
            "image/png",
            new byte[0]
        );

        when(s3Service.uploadFile(file)).thenReturn(product.getFileKey());
        when(s3Service.getPresignedUrl(product.getFileKey())).thenReturn(productDto.fileUrl());
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

        Product product = Product.builder()
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

        ProductDto productDto = ProductDto.builder()
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

        Product product = Product.builder()
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

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(id);

        // then
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }
}
