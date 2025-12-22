package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertySearchDto;
import com.ssafy.tothezip.property.model.mapper.PropertyMapper;
import com.ssafy.tothezip.region.model.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertySearchServiceImpl implements PropertySearchService {

    private final PropertyMapper propertyMapper;
    private final RegionMapper regionMapper;

    // 검색바에서 아파트 이름 자동완성
    @Override
    public List<PropertySearchDto.AptItem> search(String query, String sido, String gugun, String dong) {
        String q = (query == null) ? "" : query.trim();
        if (q.isEmpty()) return Collections.emptyList();

        String dongCode = null;
        String sggCd = null;
        List<String> sggCdList = null;

        boolean hasSido = sido != null && !sido.isBlank();
        boolean hasGugun = gugun != null && !gugun.isBlank();
        boolean hasDong = dong != null && !dong.isBlank();

        if (hasSido && hasGugun && hasDong) {
            dongCode = regionMapper.selectDongCode(sido, gugun, dong); // 10자리
        } else if (hasSido && hasGugun) {
            sggCd = regionMapper.selectSggCdBySidoGugun(sido, gugun); // 5자리
        } else if (hasSido) {
            sggCdList = regionMapper.selectSggCdsBySido(sido); // 5자리 리스트
        }

        return propertyMapper.searchApartments(q, dongCode, sggCd, sggCdList, 20);
    }

    // 지도화면에서 보여주는 검색결과
    @Override
    public List<PropertySearchDto.BuildingCard> searchBuildings(PropertySearchDto.SearchRequest req) {

        // 지역코드 해석(dongCode/sggCd/sggCdList)
        String dongCode = null;
        String sggCd = null;
        List<String> sggCdList = null;

        boolean hasSido = req.getSido() != null && !req.getSido().isBlank();
        boolean hasGugun = req.getGugun() != null && !req.getGugun().isBlank();
        boolean hasDong = req.getDong() != null && !req.getDong().isBlank();

        if (hasSido && hasGugun && hasDong) {
            dongCode = regionMapper.selectDongCode(req.getSido(), req.getGugun(), req.getDong());
        } else if (hasSido && hasGugun) {
            sggCd = regionMapper.selectSggCdBySidoGugun(req.getSido(), req.getGugun());
        } else if (hasSido) {
            sggCdList = regionMapper.selectSggCdsBySido(req.getSido());
        }

        // 주변시설 토글 -> tag_id 리스트 만들기
        List<Integer> facilityTagIds = new ArrayList<>();
        if (Boolean.TRUE.equals(req.getNearSubway()))  facilityTagIds.add(1);
        if (Boolean.TRUE.equals(req.getNearHospital())) facilityTagIds.add(2);
        if (Boolean.TRUE.equals(req.getNearSchool())) facilityTagIds.add(3);
        if (Boolean.TRUE.equals(req.getNearCulture())) facilityTagIds.add(4);

        return propertyMapper.searchBuildings(req, dongCode, sggCd, sggCdList, facilityTagIds);
    }

    public List<PropertySearchDto.ListingItem> getListingsByAptSeq(String aptSeq) {
        return propertyMapper.selectListingsByAptSeq(aptSeq);
    }

}
