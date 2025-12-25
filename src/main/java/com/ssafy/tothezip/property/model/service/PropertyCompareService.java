package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertyCompareDto;

public interface PropertyCompareService {

    /**
     * 비교 매물 후보 조회
     * GET /property/listings/{propertyId}/comparisons?limit=10
     *
     * @param propertyId 기준 매물 id
     * @param userId 로그인 유저 id (비로그인이면 null)
     * @param limit 최대 후보 개수 (서버에서 1~30으로 보정)
     */
    PropertyCompareDto.CompareResponse getComparisons(Integer propertyId, Integer userId, int limit);
}
