package com.example.ecommerce.service;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.exception.file.FileContentTypeMismatchException;
import com.example.ecommerce.common.exception.file.FileException;
import com.example.ecommerce.common.exception.file.FileIsEmptyException;
import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import com.example.ecommerce.common.exception.product.ProductServiceBusinessException;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.custom.ProductRepositoryCustom;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductRepositoryCustom productRepositoryCustom;
    private final S3Service s3Service;

    @Transactional
    @Override
    public ProductDto createProduct(CreateProductDto createProductDto, MultipartFile file) {
        ProductDto productDto;

        try {
            log.info("ProductService::createProduct execution started.");

            checkFileValidation(file);

            String fileKey = s3Service.uploadFile(file);
            log.debug("S3Service::uploadFile generated fileKey : {}", fileKey);

            Product product = createProductDto.toEntity(createProductDto, file, fileKey);
            log.debug("ProductService::createProduct toEntity converter parameters : ({} {} {})",
                createProductDto, file, fileKey);

            Product result = productRepository.save(product);
            log.debug("ProductService::createProduct received response from Database : {}",
                result);

            productDto = Product.toDto(result);
            log.debug("ProductService::createProduct toDto converter parameter : ({})", productDto);
        } catch (Exception ex) {
            log.error(
                "Exception occurred while persisting product to Database, Exception message : {}",
                ex.getMessage());
            throw new ProductServiceBusinessException(
                "Exception occurred while create a new product");
        }

        log.info("ProductService::createProduct execution successfully ended.");
        return productDto;
    }

    @Override
    public PageableDto<ProductDto> searchProducts(String keyword, Category category,
        Size productSize, Pageable pageable, String entryPoint) {
        PageableDto<Product> productDtoPageableDto = productRepositoryCustom.searchProducts(keyword,
            category, productSize, pageable, entryPoint);

        List<ProductDto> productDtoList = convertToProductDtoList(productDtoPageableDto.data());

        return new PageableDto<>(productDtoList, productDtoPageableDto.last(),
            productDtoPageableDto.page(), productDtoPageableDto.size());
    }

    @Override
    public PageableDto<ProductDto> getAllProducts(Pageable pageable) {
        Page<Product> pageableProducts = productRepository.findAll(pageable);

        return PageableDto.toDto(pageableProducts.map(Product::toDto));
    }

    @Override
    public PageableDto<ProductDto> getShopDisplayableProducts(Pageable pageable) {
        List<Product> products = productRepository.findAll();

        List<Product> shopDisplayableProducts = products.stream()
            .filter(p -> p != null && Boolean.TRUE.equals(p.getShopDisplayable()))
            .toList();

        log.info("쇼핑몰 노출 상품 목록 : " + shopDisplayableProducts);

        List<ProductDto> productDtoList = convertToProductDtoList(shopDisplayableProducts);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productDtoList.size());
        List<ProductDto> pagedList = productDtoList.subList(start, end);

        Page<ProductDto> productDtoPage = new PageImpl<>(pagedList, pageable,
            productDtoList.size());

        return PageableDto.toDto(productDtoPage);
    }

    @Override
    public ProductDto getProduct(Long id) {
        Product product = findProductById(id);

        String fileKey = product.getFileKey();

        // S3에서 파일 URL을 생성
        String fileUrl = s3Service.getPresignedUrl(fileKey);

        String fileName = product.getFileName();

        return Product.toDto(product, fileName, fileUrl);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto, MultipartFile file) {
        String fileKey = null;
        String presignedUrl = null;

        if (file != null) {
            // 파일을 S3에 업로드
            fileKey = s3Service.uploadFile(file);

            log.info("AWS S3 - generated fileKey : " + fileKey);

            presignedUrl = s3Service.getPresignedUrl(fileKey);

            log.info("AWS S3 - generated presignedUrl : " + fileKey);
        }

        Product product = findProductById(id);

        product.update(productDto, file != null ? file.getOriginalFilename() : null, fileKey);

        return Product.toDto(product, product.getFileName(), presignedUrl);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);

        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(ProductException.NOTFOUND.getStatus(),
                ProductException.NOTFOUND.getMessage()));
    }

    protected List<ProductDto> convertToProductDtoList(List<Product> productDtoPageableDto) {
        return productDtoPageableDto.stream()
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
}
