package com.example.ecommerce.service;

import com.example.ecommerce.common.exception.product.ProductException;
import com.example.ecommerce.common.exception.product.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final S3Service s3Service;

    @Transactional
    @Override
    public Long createProduct(CreateProductDto createProductDto, MultipartFile file) {
        // 파일을 S3에 업로드
        String fileKey = s3Service.uploadFile(file);

        log.info("AWS S3 - generated fileKey : " + fileKey);

        Product product = CreateProductDto.toEntity(createProductDto, file, fileKey);

        log.info("dto로부터 변환된 Product 엔티티 :" + product);

        return productRepository.save(product).getId();
    }

    @Override
    public PageableDto<ProductDto> getAllProducts(Pageable pageable) {
        Page<Product> pageableProducts = productRepository.findAll(pageable);

        return PageableDto.toDto(pageableProducts.map(Product::toDto));
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
        String fileKey="";
        
        if(file!=null){
            // 파일을 S3에 업로드
            fileKey = s3Service.uploadFile(file);

            log.info("AWS S3 - generated fileKey : " + fileKey);
        }

        Product product = findProductById(id);
        
        product.update(productDto, file != null ? file.getOriginalFilename() : null, fileKey);

        return Product.toDto(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
        Product product = findProductById(id);

        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ProductException.NOTFOUND.getStatus(), ProductException.NOTFOUND.getMessage()));
    }
}
