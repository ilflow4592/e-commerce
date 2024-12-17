package com.example.ecommerce.common.enums.product;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, String> {

    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null) {
            return null;
        }
        return category.getCategory();
    }

    @Override
    public Category convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }

        for (Category category : Category.values()) {
            if (category.getCategory().equals(dbData)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + dbData);
    }
}

