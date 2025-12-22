package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertySearchDto;
import com.ssafy.tothezip.property.model.mapper.PropertyMapper;
import com.ssafy.tothezip.region.model.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertySearchServiceImpl implements PropertySearchService {

    private final PropertyMapper propertyMapper;
    private final RegionMapper regionMapper;

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
            dongCode = regionMapper.selectDongCode(sido, gugun, dong);
        } else if (hasSido && hasGugun) {
            sggCd = regionMapper.selectSggCdBySidoGugun(sido, gugun);
        } else if (hasSido) {
            sggCdList = regionMapper.selectSggCdsBySido(sido);
        }

        return propertyMapper.searchApartments(q, dongCode, sggCd, sggCdList, 20);
    }
}
