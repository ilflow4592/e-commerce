package com.example.ecommerce.dto.product;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.common.validator.EnumValidator;
import com.example.ecommerce.entity.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record CreateProductDto(
    @NotBlank(message = "상품 이름이 빈 문자열일 수 없습니다.")
    String name,
    @NotBlank(message = "상품 설명이 빈 문자열일 수 없습니다.")
    String description,
    @NotNull
    @Min(value = 10000, message = "개당 가격은 10,000원 이상입니다.")
    Integer unitPrice,
    @NotNull
    @Min(value = 1, message = "재고는 0 이하일 수 없습니다.")
    Integer stockQuantity,
    @NotNull
    @EnumValidator(target = Category.class, message = "해당 값은 Category 열거형에 존재하지 않습니다. 다시 시도해 주세요.")
    String category,
    @NotNull
    @EnumValidator(target = Size.class, message = "해당 값은 Size 열거형에 존재하지 않습니다. 다시 시도해 주세요.")
    String size,
    @Nullable
    Float avgRating,
    @NotNull
    Boolean shopDisplayable
) {

    public static Product toEntity(CreateProductDto dto, MultipartFile file, String fileKey) {
        return Product.builder()
            .name(dto.name)
            .description(dto.description)
            .unitPrice(dto.unitPrice)
            .stockQuantity(dto.stockQuantity)
            .category(Category.valueOf(dto.category))
            .size(Size.valueOf(dto.size))
            .avgRating(dto.avgRating == null ? 0.0f : dto.avgRating) //초기값
            .shopDisplayable(dto.shopDisplayable)
            .fileName(file.getOriginalFilename())
            .fileKey(fileKey)
            .build();
    }
}
