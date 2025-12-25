package com.ssafy.tothezip.property.model;

import lombok.Data;
import java.util.List;

public class PropertyDto {

    @Data
    public static class RecommendationsProperty {
        private String regionName;
        private List<String> facilityTags;
        private List<PropertyCardDto> properties;
        private PreferencesDto preferences;
    }
}