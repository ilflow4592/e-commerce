package com.example.ecommerce.repository.custom;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.entity.Product;
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

    /**
     * 검색 시, Full-Text 인덱스 활용을 위한 네이티브 쿼리 작성
     */
    @Override
    public PageableDto<Product> searchProducts(String keyword, Category category, Size productSize, Pageable pageable, String entryPoint) {
        // 기본 네이티브 쿼리와 COUNT 쿼리 공통 부분 생성
        String baseQuery = "MATCH(name) AGAINST (:keyword IN BOOLEAN MODE)";

        // 조건 추가
        StringBuilder filterConditions = new StringBuilder();

        if (category != null) {
            filterConditions.append(" AND category = :category");
        }
        if (productSize != null) {
            filterConditions.append(" AND product_size = :product_size");
        }
        if(entryPoint != null && entryPoint.equals("shop")){
                filterConditions.append(" AND shop_displayable = 1");
        }

        // 데이터 조회 쿼리
        String dataQuery = String.format("SELECT * FROM products WHERE %s%s LIMIT :offset, :size", baseQuery, filterConditions);

        Query query = entityManager.createNativeQuery(dataQuery, Product.class)
                .setParameter("keyword", keyword)
                .setParameter("offset", pageable.getOffset())
                .setParameter("size", pageable.getPageSize());

        // COUNT 쿼리
        String countQuery = String.format("SELECT COUNT(*) FROM products WHERE %s%s", baseQuery, filterConditions);

        Query countResultQuery = entityManager.createNativeQuery(countQuery)
                .setParameter("keyword", keyword);

        // 공통 파라미터 설정
        if (category != null) {
            query.setParameter("category", category.getCategory());
            countResultQuery.setParameter("category", category.getCategory());
        }
        if (productSize != null) {
            query.setParameter("product_size", productSize.toString());
            countResultQuery.setParameter("product_size", productSize.toString());
        }

        // 결과 조회 및 총 데이터 수 계산
        List<Product> products = query.getResultList();
        long totalElements = ((Number) countResultQuery.getSingleResult()).longValue();

        // 페이징 처리
        Page<Product> pageableProducts = new PageImpl<>(
                products,
                pageable,
                totalElements
        );

        return PageableDto.toDto(pageableProducts);
    }
}
