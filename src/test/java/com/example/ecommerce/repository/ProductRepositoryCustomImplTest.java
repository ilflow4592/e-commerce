package com.example.ecommerce.repository;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.custom.ProductRepositoryCustomImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductRepositoryCustomImplTest {

    @Autowired
    private ProductRepositoryCustomImpl productRepositoryCustom;

    @Test
    @DisplayName("상품 검색 및 필터링을 할 수 있다. (shopDisplayable - true)")
    void testSearchProducts() {
        // given
        String keyword = "티셔츠";
        Category category = Category.TOPS;
        Size productSize = Size.M;
        Pageable pageable = PageRequest.of(0, 10);
        String entryPoint = "shop";

        // when
        PageableDto<Product> result = productRepositoryCustom.searchProducts(keyword, category, productSize, pageable, entryPoint);

        // then
        assertNotNull(result);
        assertEquals(result.data().size(),1);
        assertEquals(10, result.size());
        assertEquals(1, result.page());
    }

    @Test
    @DisplayName("entryPoint가 shop이 아닐 시 어드민에서 노출되는 상품 위주로 조회한다. (shopDisplayable - false,true 포함)")
    void testSearchProducts_entryPoint() {
        // given
        String keyword = "티셔츠";
        Category category = Category.TOPS;
        Size productSize = Size.M;
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageableDto<Product> result = productRepositoryCustom.searchProducts(keyword, category, productSize, pageable, null);

        // then
        assertNotNull(result);
        assertEquals(result.data().size(),2);
        assertEquals(result.data().get(0).getShopDisplayable(), true);
        assertEquals(10, result.size());
        assertEquals(1, result.page());
    }
}
