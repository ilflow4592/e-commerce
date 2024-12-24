package com.example.ecommerce.repository;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.example.ecommerce.repository.custom.ProductRepositoryCustomImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ProductRepositoryCustomImplTest {

    @Autowired
    private ProductRepositoryCustomImpl productRepositoryCustom;

    @Test
    @DisplayName("상품 검색 및 필터링을 할 수 있다.")
    void testSearchProducts() {
        // given
        String keyword = "청바지";
        Category category = Category.PANTS;
        Size productSize = Size.M;
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageableDto<ProductDto> result = productRepositoryCustom.searchProducts(keyword, category, productSize, pageable);

        // then
        assertNotNull(result);
        Assertions.assertEquals(10, result.size());
        Assertions.assertEquals(1, result.page());
    }

    @Test
    @DisplayName("null이 포함된 키워드로 검색, 필터링을 수행 시 반환되는 데이터가 없다.")
    void testSearchProducts_withNullKeywords() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        PageableDto<ProductDto> result = productRepositoryCustom.searchProducts(null, null, null, pageable);

        // then
        assertNotNull(result);
        Assertions.assertEquals(0, result.data().size());
    }
}
