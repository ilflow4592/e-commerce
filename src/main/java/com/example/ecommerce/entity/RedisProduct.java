package com.example.ecommerce.entity;


import com.example.ecommerce.common.enums.product.Category;
import com.example.ecommerce.common.enums.product.Size;
import com.example.ecommerce.dto.product.ProductDto;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "product", timeToLive = 3600) // 1시간 캐싱
public class RedisProduct implements Serializable {

    @Id
    private Long id;
    private String name;
    private String description;
    private Integer unitPrice;
    private Integer stockQuantity;
    private Category category;
    private Size size;
    private Float avgRating;
    private Boolean shopDisplayable;
    private String fileName;
    private String fileKey;

    public ProductDto toDto(String fileUrl) {
        return ProductDto.builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .unitPrice(this.unitPrice)
            .stockQuantity(this.stockQuantity)
            .category(String.valueOf(this.category))
            .size(String.valueOf(this.size))
            .shopDisplayable(this.shopDisplayable)
            .fileName(this.fileName)
            .fileUrl(fileUrl)
            .build();
    }
}
