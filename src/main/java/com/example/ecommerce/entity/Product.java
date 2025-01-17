package com.example.ecommerce.entity;

import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.CategoryConverter;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.product.ProductDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Entity(name="products")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer unitPrice;

    @NotNull
    private Integer stockQuantity;

    @NotNull
    @Convert(converter = CategoryConverter.class)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "productSize")
    private Size size;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @NotNull
    private Float avgRating;

    @NotNull
    private Boolean shopDisplayable;

//    @NotNull
    private String fileKey;

    public static ProductDto toDto(Product product) {
        return toDto(product, null);
    }

    public static ProductDto toDto(Product product, String fileUrl){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .size(product.getSize())
                .shopDisplayable(product.getShopDisplayable())
                .fileUrl(fileUrl)
                .build();
    }

    public void update(ProductDto dto){
        if (dto.name() != null) this.name = dto.name();
        if (dto.description() != null) this.description = dto.description();
        if (dto.unitPrice() != null) this.unitPrice = dto.unitPrice();
        if (dto.stockQuantity() != null) this.stockQuantity = dto.stockQuantity();
        if (dto.category() != null) this.category = dto.category();
        if (dto.size() != null) this.size = dto.size();
    }

    public void updateStockQuantity(Integer stockQuantity){
        this.stockQuantity = stockQuantity;
    }

    public void updateAvgRating(float avgRating) {this.avgRating = avgRating;}


}
