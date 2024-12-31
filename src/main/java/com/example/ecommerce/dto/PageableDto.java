package com.example.ecommerce.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageableDto<T>(
        List<T> data,
        boolean last,
        int page,
        int size
) {

    public static <T> PageableDto<T> toDto(Page<T> page) {
        return PageableDto.<T>builder()
                .data(page.getContent())
                .last(page.isLast())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .build();
    }

}
