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



@AllArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public Long createProduct(CreateProductDto createProductDto, String fileKey) {
        Product product = CreateProductDto.toEntity(createProductDto, fileKey);

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

        return Product.toDto(product);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = findProductById(id);

        product.update(productDto);

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
