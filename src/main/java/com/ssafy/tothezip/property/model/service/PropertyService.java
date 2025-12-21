package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertyDto;

public interface PropertyService {
    PropertyDto.RecommendationsProperty getHomeRecommendations(Integer userId);
}
