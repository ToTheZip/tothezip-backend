package com.ssafy.tothezip.property.model.mapper;

import com.ssafy.tothezip.property.model.PropertyCardDto;
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
                                                   @Param("limit") int limit);

    List<String> selectPropertyTagNames(@Param("aptSeq") String aptSeq);
}
