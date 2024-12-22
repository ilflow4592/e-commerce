package com.example.ecommerce.repository.custom;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Primary
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public PageableDto<ProductDto> searchProducts(String keyword, Pageable pageable) {
        // 네이티브 쿼리를 사용한 Full-Text 검색
        String nativeQuery = """
            SELECT *
            FROM products
            WHERE MATCH(name) AGAINST (:keyword IN BOOLEAN MODE)
            LIMIT :offset, :size
        """;

        Query query = entityManager.createNativeQuery(nativeQuery, Product.class)
                .setParameter("keyword", keyword)
                .setParameter("offset", pageable.getOffset())
                .setParameter("size", pageable.getPageSize());

        List<Product> products = query.getResultList();

        Page<Product> pageableProducts = new PageImpl<>(
                products,  // 현재 페이지의 데이터
                pageable,   // 페이지 요청 정보
                products.size()    // 전체 데이터 수
        );

        return PageableDto.toDto(pageableProducts.map(Product::toDto));

    }
}
