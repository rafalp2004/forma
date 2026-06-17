package com.example.demo.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OffResponse {
    private List<OffProduct> products;

    @Data
    public static class OffProduct {
        private String code;

        @JsonProperty("product_name")
        private String productName;

        private Nutriments nutriments;
    }

    @Data
    public static class Nutriments {
        @JsonProperty("energy-kcal_100g")
        private Float calories;

        @JsonProperty("proteins_100g")
        private Float proteins;

        @JsonProperty("carbohydrates_100g")
        private Float carbohydrates;

        @JsonProperty("fat_100g")
        private Float fat;
    }
}