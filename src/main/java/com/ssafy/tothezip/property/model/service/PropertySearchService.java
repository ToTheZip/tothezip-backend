package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertySearchDto;
import java.util.List;

public interface PropertySearchService {

    // 검색바에서 아파트 이름 자동완성
    List<PropertySearchDto.AptItem> search(String query, String sido, String gugun, String dong);

    // 검색 결과 가져오기
    List<PropertySearchDto.BuildingCard> searchBuildings(PropertySearchDto.SearchRequest req);

    List<PropertySearchDto.ListingItem> getListingsByAptSeq(String aptSeq);
}

