package com.example.ecommerce.controller;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.PageableDto;
import com.example.ecommerce.dto.product.CreateProductDto;
import com.example.ecommerce.dto.product.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static String URL = "/api/v1/products";

    @Test
    void createProduct_success() throws Exception {
        //given
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("치노 팬츠")
                .description("스타일리시한 슬림 핏으로 다양한 코디에 활용 가능합니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.PANTS)
                .size(Size.M)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String jsonPayload = objectMapper.writeValueAsString(createProductDto);

        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

        //when
        ResponseEntity<Long> response = restTemplate.exchange(
                URL, // 요청 URL
                HttpMethod.POST, // HTTP 메서드
                request, // 요청 본문
                Long.class // 응답 타입
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    void searchProducts_withValidParams_success() throws JsonProcessingException {
        //given
        String keyword = "치노 팬츠";
        Category category = Category.PANTS;
        Size productSize = Size.M;

        int page = 1;
        int size = 10;

        String queryString = "/search?keyword=" + keyword + "&category=" + category + "&productSize=" + productSize + "&page=" + page + "&size=" + size;

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                URL + queryString,
                HttpMethod.GET,
                null, // GET 요청에는 body가 없으므로 null
                String.class
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        PageableDto<ProductDto> pageableDto = objectMapper.readValue(response.getBody(), PageableDto.class);
        assertThat(pageableDto).isNotNull();
        assertThat(pageableDto.page()).isEqualTo(1);
        assertThat(pageableDto.size()).isEqualTo(10);
    }

    @Test
    void getAllProducts_success() throws Exception {
        //given
        int page = 1;
        int size = 10;

        String queryString = "?page=" + page + "&size=" + size;

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                URL + queryString,
                HttpMethod.GET,
                null,
                String.class
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        PageableDto<ProductDto> pageableDto = objectMapper.readValue(response.getBody(), PageableDto.class);
        assertThat(pageableDto).isNotNull();
        assertThat(pageableDto.page()).isEqualTo(1);
        assertThat(pageableDto.size()).isEqualTo(10);
    }

    @Test
    void getProduct_success() {
        //given
        Long productId = 1L;

        //when
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                URL + "/{id}",
                HttpMethod.GET,
                null,
                ProductDto.class,
                productId
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updateProduct_success() {
        //given
        Long productId = 1L;

        ProductDto productDto = ProductDto.builder()
                .name("패딩 점퍼")
                .description("방한용으로 착용하기 좋은 따뜻한 패딩 점퍼입니다.")
                .unitPrice(50000)
                .stockQuantity(100)
                .category(Category.OUTER)
                .size(Size.L)
                .build();

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDto> requestEntity = new HttpEntity<>(productDto, headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(
                URL + "/{id}",
                HttpMethod.PATCH,
                requestEntity,
                ProductDto.class,
                productId
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("패딩 점퍼");
    }

    @Test
    void deleteProduct_success() {
        //given
        Long productId = 1L;

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                URL + "/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                productId
        );

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("id = " + productId + "인 상품이 성공적으로 삭제되었습니다.");
    }
}