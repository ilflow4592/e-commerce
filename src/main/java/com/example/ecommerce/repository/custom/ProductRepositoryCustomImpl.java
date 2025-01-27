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
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM products WHERE ");
        StringBuilder countQueryBuilder = new StringBuilder("SELECT COUNT(*) FROM products WHERE ");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            queryBuilder.append("MATCH(name) AGAINST (:keyword IN BOOLEAN MODE)");
            countQueryBuilder.append("MATCH(name) AGAINST (:keyword IN BOOLEAN MODE)");
        } else {
            queryBuilder.append("1=1");
            countQueryBuilder.append("1=1");
        }

        if (category != null) {
            queryBuilder.append(" AND category = :category");
            countQueryBuilder.append(" AND category = :category");
        }
        if (productSize != null) {
            queryBuilder.append(" AND product_size = :product_size");
            countQueryBuilder.append(" AND product_size = :product_size");
        }
        if ("shop".equals(entryPoint)) {
            queryBuilder.append(" AND shop_displayable = 1");
            countQueryBuilder.append(" AND shop_displayable = 1");
        }

        // LIMIT 및 OFFSET 추가
        queryBuilder.append(" LIMIT :offset, :size");

        Query query = entityManager.createNativeQuery(queryBuilder.toString(), Product.class);
        Query countResultQuery = entityManager.createNativeQuery(countQueryBuilder.toString());

        // 파라미터 설정
        if (hasKeyword) {
            query.setParameter("keyword", keyword);
            countResultQuery.setParameter("keyword", keyword);
        }
        if (category != null) {
            System.out.println("category = " + category.getCategory());
            query.setParameter("category", category.getCategory());
            countResultQuery.setParameter("category", category.getCategory());
        }
        if (productSize != null) {
            query.setParameter("product_size", productSize.toString());
            countResultQuery.setParameter("product_size", productSize.toString());
        }

        query.setParameter("offset", Math.toIntExact(pageable.getOffset()));
        query.setParameter("size", pageable.getPageSize());

        // 결과 조회 및 페이징 처리
        List<Product> products = query.getResultList();
        System.out.println("products = " + products);
        long totalElements = ((Number) countResultQuery.getSingleResult()).longValue();

        Page<Product> pageableProducts = new PageImpl<>(products, pageable, totalElements);
        return PageableDto.toDto(pageableProducts);
    }


}
