package com.ssafy.tothezip.property.model.mapper;

import com.ssafy.tothezip.property.model.PropertyCardDto;
import com.ssafy.tothezip.property.model.PropertySearchDto;
import com.ssafy.tothezip.property.model.RegionDto;
import com.ssafy.tothezip.property.model.TagDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyMapper {

    // 비회원
    List<PropertyCardDto> selectTopRatedPropertiesAll(@Param("limit") int limit);


    String selectUserRegionName(@Param("userId") Integer userId);

    List<String> selectUserFacilityNames(@Param("userId") Integer userId);

    List<Integer> selectUserFacilityTagIds(@Param("userId") Integer userId);

    String selectSggCdBySidoGugun(@Param("sido") String sido,
                                  @Param("gugun") String gugun);

    List<PropertyCardDto> selectTopRatedProperties(@Param("sggCd") String sggCd,
                                                   @Param("facilityTagIds") List<Integer> facilityTagIds,
                                                   @Param("limit") int limit,
                                                   @Param("userId") Integer userId,
                                                   @Param("hasPreference") boolean hasPreference);

    List<String> selectPropertyTagNames(@Param("aptSeq") String aptSeq);

    // 검색할 때 아파트 이름 받아오기 위함
    List<PropertySearchDto.AptItem> searchApartments(
            @Param("query") String query,
            @Param("dongCode") String dongCode,     // 10자리(선택)
            @Param("sggCd") String sggCd,           // 5자리(선택)
            @Param("sggCdList") List<String> sggCdList, // 시도만 선택 시(선택)
            @Param("limit") int limit
    );
    // 지역
    List<String> selectDistinctSido();

    List<String> selectGugunBySido(String sido);

    // 태그
    List<TagDto> selectTags(@Param("type") String type);

}
