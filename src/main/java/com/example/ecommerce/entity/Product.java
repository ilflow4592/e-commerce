package com.example.ecommerce.entity;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.CategoryConverter;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.product.ProductDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity(name="products")
@AttributeOverride(name = "id", column = @Column(name = "productId"))
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity{

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer unitPrice;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Convert(converter = CategoryConverter.class)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Size size;

    public static ProductDto toDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }

    public void update(ProductDto dto){
        if (dto.name() != null) this.name = dto.name();
        if (dto.description() != null) this.description = dto.description();
        if (dto.unitPrice() != null) this.unitPrice = dto.unitPrice();
        if (dto.stockQuantity() != null) this.stockQuantity = dto.stockQuantity();
    }

    public void updateStockQuantity(Integer stockQuantity){
        this.stockQuantity = stockQuantity;
    }


}
