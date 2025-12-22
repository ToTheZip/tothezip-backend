package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertySearchDto;
import java.util.List;

public interface PropertySearchService {
    List<PropertySearchDto.AptItem> search(String query, String sido, String gugun, String dong);
}
