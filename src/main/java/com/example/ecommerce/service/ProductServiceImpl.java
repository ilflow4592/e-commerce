package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.exception.file.FileContentTypeMismatchException;
import com.example.ecommerce.common.exception.file.FileException;
import com.example.ecommerce.common.exception.file.FileIsEmptyException;
import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.custom.ProductRepositoryCustom;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
@CacheConfig(cacheNames = "products", cacheManager = "cacheManager")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductRepositoryCustom productRepositoryCustom;
    private final S3Service s3Service;

    @Transactional
    @Override
    // 캐싱 무효화 : 다음 조회 시 최신 데이터를 가져오도록 함
    @Caching(evict = {
        @CacheEvict(key = "'all'"),
        @CacheEvict(key = "'shopDisplayable'"),
        @CacheEvict(allEntries = true) // 검색 결과 캐시 모두 무효화
    })
    public ProductDto createProduct(CreateProductDto createProductDto, MultipartFile file) {
        log.info("ProductService::createProduct execution started.");

        ProductDto productDto;

        checkFileValidation(file);

        String fileKey = s3Service.uploadFile(file);
        log.debug("Called - s3Service.uploadFile(file), response - fileKey : {}", fileKey);

        Product product = createProductDto.toEntity(createProductDto, file, fileKey);
        log.debug(
            "Called - createProductDto.toEntity(createProductDto, file, fileKey), converter parameters : ({} {} {})",
            createProductDto, file, fileKey);

        Product result = productRepository.save(product);
        log.debug(
            "Called - productRepository.save(product), response from database - result : {}",
            result);

        productDto = Product.toDto(result);
        log.debug("Called - Product.toDto(result),  converter parameter : ({})", productDto);

        log.info("ProductService::createProduct execution successfully ended.");

        return productDto;
    }

    @Override
    @Cacheable(key = "'search:' + #keyword + ':' + #category + ':' + #productSize + ':' + #pageable.pageNumber + ':' + #entryPoint")
    public PageableDto<ProductDto> searchProducts(String keyword, Category category,
        Size productSize, Pageable pageable, String entryPoint) {
        log.info("ProductService::searchProducts execution started.");

        PageableDto<Product> productDtoPageableDto = productRepositoryCustom.searchProducts(keyword,
            category, productSize, pageable, entryPoint);
        log.debug(
            "Called - productRepositoryCustom.searchProducts(keyword, category, productSize, pageable, entryPoint(), response : productDtoPageableDto : {}",
            productDtoPageableDto);

        List<ProductDto> productDtoList = convertToProductDtoList(productDtoPageableDto.data());
        log.debug(
            "Called - convertToProductDtoList(productDtoPageableDto.data()), response - productDtoList : {}",
            productDtoList);

        PageableDto<ProductDto> returnedProductDtoPageableDto = new PageableDto<>(productDtoList,
            productDtoPageableDto.last(),
            productDtoPageableDto.page(), productDtoPageableDto.size());
        log.debug(
            """
                Called - new PageableDto<>(productDtoList,
                            productDtoPageableDto.last(),
                            productDtoPageableDto.page(), productDtoPageableDto.size()), response - returnedProductDtoPageableDto : {}""",
            returnedProductDtoPageableDto);

        log.info("ProductService::searchProducts execution successfully ended.");

        return returnedProductDtoPageableDto;
    }

    @Override
    @Cacheable(key = "'all'")
    public PageableDto<ProductDto> getAllProducts(Pageable pageable) {
        log.info("ProductService::getAllProducts execution started.");

        Page<Product> pageableProducts = productRepository.findAll(pageable);
        log.debug(
            "Called - productRepository.findAll(pageable), response - pageableProducts : {}",
            pageableProducts);

        log.info("ProductService::createProduct execution successfully ended.");

        return PageableDto.toDto(pageableProducts.map(Product::toDto));
    }


    @Override
    @Cacheable(key = "'shopDisplayable'")
    public PageableDto<ProductDto> getShopDisplayableProducts(Pageable pageable) {
        log.info("ProductService::getShopDisplayableProducts execution started.");

        List<Product> products = productRepository.findAll();
        log.debug("Called - productRepository.findAll(), response - products : {}", products);

        List<Product> shopDisplayableProducts = products.stream()
            .filter(p -> p != null && Boolean.TRUE.equals(p.getShopDisplayable()))
            .toList();
        log.debug("""
                Called - products.stream()
                .filter(p -> p != null && Boolean.TRUE.equals(p.getShopDisplayable()))
                .toList();, response - shopDisplayableProducts : {}""",
            shopDisplayableProducts);

        List<ProductDto> productDtoList = convertToProductDtoList(shopDisplayableProducts);
        log.debug(
            "Called - convertToProductDtoList(productDtoPageableDto.data()), response - productDtoList : {}",
            productDtoList);

        Page<ProductDto> productDtoPage = generateProductDtoPage(
            pageable, productDtoList);
        log.debug(
            "Called - generateProductDtoPage(pageable, productDtoList), response - productDtoPage: {}",
            productDtoPage);

        log.info("ProductService::getShopDisplayableProducts execution successfully ended.");

        return PageableDto.toDto(productDtoPage);
    }


    @Override
    @Cacheable(key = "#id", unless = "#result == null") // 조회 시 값이 null이면 캐싱을 하지 않음
    public ProductDto getProduct(Long id) {
        log.info("ProductService::getProduct execution started.");

        Product product = findProductById(id);
        log.debug(
            "Called - productRepository.findById(id), response from database - product : {}",
            product);

        String fileKey = product.getFileKey();
        log.debug("Called - product.getFileKey(), response - fileKey : {}", fileKey);

        String fileUrl = s3Service.getPresignedUrl(fileKey);
        log.debug("Called - s3Service.getPresignedUrl(fileKey), response - fileUrl : {}",
            fileUrl);

        String fileName = product.getFileName();
        log.debug("Called - product.getFileName(), response - fileName : {}", fileName);

        log.info("ProductService::getProduct execution successfully ended.");

        return Product.toDto(product, fileName, fileUrl);
    }

    @Transactional
    @Override
    @Caching(evict = {
        @CacheEvict(key = "'all'"),
        @CacheEvict(key = "'shopDisplayable'"),
        @CacheEvict(key = "#id")
    })
    public ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile file) {
        log.info("ProductService::updateProduct execution started.");

        String fileKey = null;
        String presignedUrl = null;

        // 파일을 S3에 업로드 후, presignedUrl 반환
        if (file != null) {
            fileKey = s3Service.uploadFile(file);
            presignedUrl = s3Service.getPresignedUrl(fileKey);
        }

        Product product = findProductById(id);
        log.debug("Called - productRepository.findById(id), response from database - product : {}",
            product);

        product.update(productDto, file != null ? file.getOriginalFilename() : null, fileKey);
        log.debug(
            "Called - product.update(productDto, file != null ? file.getOriginalFilename() : null, fileKey), response : NONE");

        ProductDto dto = Product.toDto(product, product.getFileName(), presignedUrl);
        log.debug(
            "Called - Product.toDto(product, product.getFileName(), presignedUrl), response - dto : {}",
            dto);

        log.info("ProductService::updateProduct execution successfully ended.");

        return dto;
    }

    @Transactional
    @Override
    @Caching(evict = {
        @CacheEvict(key = "'all'"),
        @CacheEvict(key = "'shopDisplayable'"),
        @CacheEvict(key = "#id")
    })
    public void deleteProduct(Long id) {
        log.info("ProductService::deleteProduct execution started.");

        Product product = findProductById(id);
        log.debug("Called - productRepository.findById(id), response from database - product : {}",
            product);

        productRepository.delete(product);
        log.info("Called - productRepository.delete(product), response : NONE");

        log.info("ProductService::deleteProduct execution successfully ended.");
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(ProductException.NOTFOUND.getStatus(),
                ProductException.NOTFOUND.getMessage()));
    }

    protected List<ProductDto> convertToProductDtoList(List<Product> productDtoPageableDto) {
        log.info("ProductService::convertToProductDtoList execution started.");

        List<ProductDto> productDtoList = productDtoPageableDto.stream()
            .map(product -> {
                String fileKey = product.getFileKey();
                String fileUrl = s3Service.getPresignedUrl(fileKey);

                return ProductDto.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .unitPrice(product.getUnitPrice())
                    .stockQuantity(product.getStockQuantity())
                    .shopDisplayable(product.getShopDisplayable())
                    .category(String.valueOf(product.getCategory()))
                    .size(String.valueOf(product.getSize()))
                    .fileName(product.getFileName())
                    .fileUrl(fileUrl)
                    .build();
            })
            .toList();

        log.info("ProductService::convertToProductDtoList execution successfully ended.");

        return productDtoList;
    }

    private void checkFileValidation(MultipartFile file) {
        log.info("File validation started.");

        //파일이 존재하는지 체크
        if (file.isEmpty()) {
            throw new FileIsEmptyException(FileException.EMPTY.getStatus(),
                FileException.EMPTY.getMessage());
        }

        // MIME 타입이 image/png 인지 확인
        String contentType = file.getContentType();

        if (!"image/png".equalsIgnoreCase(contentType)) {
            throw new FileContentTypeMismatchException(FileException.MISMATCH.getStatus(),
                FileException.MISMATCH.getMessage());
        }

        log.info("File validation successfully ended.");
    }

    private static Page<ProductDto> generateProductDtoPage(Pageable pageable,
        List<ProductDto> productDtoList) {
        log.info("ProductService::generateProductDtoPage execution started.");

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productDtoList.size());
        List<ProductDto> pagedList = productDtoList.subList(start, end);

        Page<ProductDto> productDtoPage = new PageImpl<>(pagedList, pageable,
            productDtoList.size());

        log.info("ProductService::generateProductDtoPage execution successfully ended.");

        return productDtoPage;
    }
}
