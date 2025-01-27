package com.example.ecommerce.repository.custom;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Primary
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final EntityManager entityManager;

    /**
     * 검색 시, Full-Text 인덱스 활용을 위한 네이티브 쿼리 작성
     */
    @Override
    public PageableDto<Product> searchProducts(String keyword, Category category, Size productSize,
        Pageable pageable, String entryPoint) {

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean isSingleCharacter = hasKeyword && keyword.trim().length() == 1;

        // 쿼리 문자열 생성 (SELECT 또는 COUNT)
        String selectQuery = buildQuery(hasKeyword, isSingleCharacter, category, productSize,
            entryPoint, false);
        String countQuery = buildQuery(hasKeyword, isSingleCharacter, category, productSize,
            entryPoint, true);

        // 네이티브 쿼리 생성
        Query query = entityManager.createNativeQuery(selectQuery, Product.class);
        Query countResultQuery = entityManager.createNativeQuery(countQuery);

        // 공통 파라미터 설정
        setParameters(query, keyword, isSingleCharacter, category, productSize, pageable);
        setParameters(countResultQuery, keyword, isSingleCharacter, category, productSize, null);

        // 결과 조회 및 페이징 처리
        List<Product> products = query.getResultList();
        long totalElements = ((Number) countResultQuery.getSingleResult()).longValue();

        Page<Product> pageableProducts = new PageImpl<>(products, pageable, totalElements);
        return PageableDto.toDto(pageableProducts);
    }

    /**
     * 쿼리를 동적으로 생성하는 메서드
     */
    private String buildQuery(boolean hasKeyword, boolean isSingleCharacter, Category category,
        Size productSize, String entryPoint, boolean isCountQuery) {

        StringBuilder queryBuilder = new StringBuilder(isCountQuery
            ? "SELECT COUNT(*) FROM products WHERE "
            : "SELECT * FROM products WHERE ");

        if (hasKeyword) {
            if (isSingleCharacter) {
                queryBuilder.append("name LIKE :keyword");
            } else {
                queryBuilder.append("MATCH(name) AGAINST (:keyword IN BOOLEAN MODE)");
            }
        } else {
            queryBuilder.append("1=1"); // 검색어가 없을 경우 기본 조건 유지
        }

        if (category != null) {
            queryBuilder.append(" AND category = :category");
        }
        if (productSize != null) {
            queryBuilder.append(" AND product_size = :product_size");
        }
        if ("shop".equals(entryPoint)) {
            queryBuilder.append(" AND shop_displayable = 1");
        }

        if (!isCountQuery) {
            queryBuilder.append(" LIMIT :offset, :size");
        }

        return queryBuilder.toString();
    }

    /**
     * 공통적으로 파라미터를 설정하는 메서드
     */
    private void setParameters(Query query, String keyword, boolean isSingleCharacter,
        Category category, Size productSize, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (isSingleCharacter) {
                query.setParameter("keyword", "%" + keyword + "%"); // LIKE 검색
            } else {
                query.setParameter("keyword", keyword); // MATCH 검색
            }
        }
        if (category != null) {
            query.setParameter("category", category.name());
        }
        if (productSize != null) {
            query.setParameter("product_size", productSize.toString());
        }
        if (pageable != null) {
            query.setParameter("offset", Math.toIntExact(pageable.getOffset()));
            query.setParameter("size", pageable.getPageSize());
        }
    }


}
