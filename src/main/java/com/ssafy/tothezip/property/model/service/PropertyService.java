package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.*;

import java.util.List;

public interface PropertyService {
    PropertyDto.RecommendationsProperty getHomeRecommendations(Integer userId);

    List<String> getSidoList();

    List<String> getGugunList(String sido);

    List<TagDto> getTagList(String type);

    List<TagDto> resolveTags(List<Integer> tagIds);

    PriceSeriesDto getPriceSeries(String aptSeq, String dealType, String period);
}
