package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PriceSeriesDto;
import com.ssafy.tothezip.property.model.PropertyDto;
import com.ssafy.tothezip.property.model.TagDto;

import java.util.List;

public interface PropertyService {
    PropertyDto.RecommendationsProperty getHomeRecommendations(Integer userId);

    List<String> getSidoList();

    List<String> getGugunList(String sido);

    List<TagDto> getTagList(String type);

    PriceSeriesDto getPriceSeries(String aptSeq, String dealType, String period);
}
