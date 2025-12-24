package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.*;
import com.ssafy.tothezip.property.model.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyMapper propertyMapper;

    @Override
    public PropertyDto.RecommendationsProperty getHomeRecommendations(Integer userId) {
        // =========================
        // 1️⃣ 비회원
        // =========================
        if (userId == null) {
            PropertyDto.RecommendationsProperty res =
                    new PropertyDto.RecommendationsProperty();

            res.setRegionName("추천 매물을 준비했어요");
            res.setFacilityTags(Collections.emptyList());
            res.setProperties(propertyMapper.selectTopRatedPropertiesAll(10));

            return res;
        }

        // =========================
        // 2️⃣ 회원
        // =========================
        String regionName = propertyMapper.selectUserRegionName(userId);
        List<String> facilityNames = propertyMapper.selectUserFacilityNames(userId);
        List<Integer> facilityTagIds = propertyMapper.selectUserFacilityTagIds(userId);

        // 관심 지역 없으면 → 비회원 fallback
        if (regionName == null || regionName.isBlank()) {
            PropertyDto.RecommendationsProperty res =
                    new PropertyDto.RecommendationsProperty();

            res.setRegionName("추천 매물을 준비했어요");
            res.setFacilityTags(Collections.emptyList());
            res.setProperties(propertyMapper.selectTopRatedPropertiesAll(10));

            return res;
        }

        // 지역 분리
        String[] parsed = splitRegion(regionName);
        String sido = parsed[0];
        String gugun = parsed[1];

        String sggCd = propertyMapper.selectSggCdBySidoGugun(sido, gugun);

        // sgg 코드 없으면 → fallback
        if (sggCd == null || sggCd.isBlank()) {
            PropertyDto.RecommendationsProperty res =
                    new PropertyDto.RecommendationsProperty();

            res.setRegionName("추천 매물을 준비했어요");
            res.setFacilityTags(Collections.emptyList());
            res.setProperties(propertyMapper.selectTopRatedPropertiesAll(10));

            return res;
        }

        // =========================
        // 3️⃣ 정상 회원 추천
        // =========================
        boolean hasPreference = (userId != null);
        List<PropertyCardDto> cards =
                propertyMapper.selectTopRatedProperties(sggCd, facilityTagIds, 10, userId, hasPreference);

        PropertyDto.RecommendationsProperty res =
                new PropertyDto.RecommendationsProperty();

        res.setRegionName("관심등록한 " + regionName + ", 추천 매물을 준비했어요");
        res.setFacilityTags(
                facilityNames != null ? facilityNames : Collections.emptyList()
        );
        res.setProperties(
                cards != null ? cards : Collections.emptyList()
        );

        return res;
    }

    @Override
    public List<String> getSidoList() {
        return propertyMapper.selectDistinctSido();
    }

    @Override
    public List<String> getGugunList(String sido) {
        return propertyMapper.selectGugunBySido(sido);
    }

    @Override
    public List<TagDto> getTagList(String type) {
        return propertyMapper.selectTags(type);
    }

    /** 예: "서울특별시 종로구" -> ["서울특별시", "종로구"] */
    private String[] splitRegion(String regionName) {
        String trimmed = regionName.trim();
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) return new String[]{parts[0], ""};

        String gugun = parts[parts.length - 1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) sb.append(' ');
            sb.append(parts[i]);
        }
        String sido = sb.toString();
        return new String[]{sido, gugun};
    }

    @Override
    public List<TagDto> resolveTags(List<Integer> tagIds) {
              if (tagIds == null || tagIds.isEmpty()) {
                       return Collections.emptyList();
                    }
                return propertyMapper.selectTagsByIds(tagIds);
            }


    @Override
    public PriceSeriesDto getPriceSeries(String aptSeq, String dealType, String period) {
        String p = (period == null || period.isBlank()) ? "month" : period;
        // 최근 5년 (오늘 기준)
        LocalDate from = LocalDate.now().minusYears(5).withDayOfMonth(1);
        String fromDate = from.toString(); // yyyy-MM-dd

        List<PriceSeriesDto.Point> points =
                "quarter".equalsIgnoreCase(p)
                        ? propertyMapper.selectPriceSeriesQuarterly(aptSeq, dealType, fromDate)
                        : propertyMapper.selectPriceSeriesMonthly(aptSeq, dealType, fromDate);

        PriceSeriesDto dto = new PriceSeriesDto();
        dto.setAptSeq(aptSeq);
        dto.setDealType(dealType);
        dto.setPeriod(p);
        dto.setFrom(fromDate);
        dto.setTo(LocalDate.now().toString());
        dto.setPoints(points);
        return dto;
    }
}
