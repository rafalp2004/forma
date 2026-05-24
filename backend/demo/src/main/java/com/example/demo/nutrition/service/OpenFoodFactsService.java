package com.example.demo.nutrition.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.nutrition.dto.OffResponse;
import com.example.demo.nutrition.model.FoodProduct;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenFoodFactsService {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<FoodProduct> searchProducts(String searchQuery) {
        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms={query}&search_simple=1&action=process&json=1&page_size=10&fields=code,product_name,nutriments";

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "FormaNutritionApp/1.0 (Politechnika Wroclawska; 272511@student.pwr.edu.pl)");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OffResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    OffResponse.class,
                    searchQuery
            );

            OffResponse response = responseEntity.getBody();
            List<FoodProduct> foundProducts = new ArrayList<>();

            if (response != null && response.getProducts() != null) {
                for (OffResponse.OffProduct offProduct : response.getProducts()) {
                    FoodProduct mappedProduct = new FoodProduct();

                    mappedProduct.setExternalId(offProduct.getCode());
                    mappedProduct.setName(offProduct.getProductName());

                    if (offProduct.getNutriments() != null) {
                        mappedProduct.setKcalPer100g(offProduct.getNutriments().getCalories() != null ? offProduct.getNutriments().getCalories() : 0.0f);
                        mappedProduct.setProteinPer100g(offProduct.getNutriments().getProteins() != null ? offProduct.getNutriments().getProteins() : 0.0f);
                        mappedProduct.setCarbsPer100g(offProduct.getNutriments().getCarbohydrates() != null ? offProduct.getNutriments().getCarbohydrates() : 0.0f);
                        mappedProduct.setFatPer100g(offProduct.getNutriments().getFat() != null ? offProduct.getNutriments().getFat() : 0.0f);
                    }

                    foundProducts.add(mappedProduct);
                }
            }
            return foundProducts;

        } catch (Exception e) {
            System.err.println("Error while fetching data from API: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}