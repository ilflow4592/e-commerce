package com.example.ecommerce.dto.product;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.validator.EnumValidator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProductDto(
    @NotNull
    Long id,
    @NotBlank(message = "상품 이름이 빈 문자열일 수 없습니다.")
    String name,
    @NotBlank(message = "상품 설명이 빈 문자열일 수 없습니다.")
    String description,
    @NotNull
    @Min(value = 10000, message = "개당 가격은 10,000원 이상입니다.")
    Integer unitPrice,
    @NotNull
    @Min(value = 1, message = "재고는 0일 수 없습니다.")
    Integer stockQuantity,
    @NotNull
    @EnumValidator(target = Category.class, message = "해당 값은 Category 열거형에 존재하지 않습니다. 다시 시도해 주세요.")
    String category,
    @NotNull
    @EnumValidator(target = Size.class, message = "해당 값은 Size 열거형에 존재하지 않습니다. 다시 시도해 주세요.")
    String size,
    @NotNull
    Boolean shopDisplayable,
    @NotNull
    String fileName,
    @NotNull
    String fileUrl,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @NotNull
    LocalDateTime createdAt
) {

}
