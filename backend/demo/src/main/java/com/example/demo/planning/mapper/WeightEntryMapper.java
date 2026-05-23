package com.example.demo.planning.mapper;

import com.example.demo.planning.dto.WeightProgressPointResponse;
import com.example.demo.planning.entity.WeightEntry;
import org.springframework.stereotype.Component;

@Component
public class WeightEntryMapper {

    public WeightProgressPointResponse toProgressResponse(WeightEntry entry) {
        return new WeightProgressPointResponse(
                entry.getDate(),
                entry.getWeightKg()
        );
    }
}
