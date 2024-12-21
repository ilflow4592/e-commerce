package com.example.ecommerce.repository.custom;

import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    private final JPAQueryFactory queryFactory;

    @Override
    public PageableDto<ProductDto> searchProducts(String keyword, Pageable pageable) {
        QProduct product = QProduct.product;

        // 페이징 정보를 활용하여 데이터 조회
        List<Product> products = queryFactory.selectFrom(product)
                .where(product.name.like("%" + keyword + "%"))
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch();

        Page<Product> pageableProducts = new PageImpl<>(
                products,  // 현재 페이지의 데이터
                pageable,             // 페이지 요청 정보
                products.size()    // 전체 데이터 수
        );

        return PageableDto.toDto(pageableProducts.map(Product::toDto));

    }
}
