package com.ssafy.tothezip.region.model.mapper;

import com.ssafy.tothezip.region.model.RegionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegionMapper {

    List<String> selectSidos();

    List<String> selectGuguns(@Param("sido") String sido);

    List<RegionDto.Dong> selectDongs(@Param("sido") String sido,
                                     @Param("gugun") String gugun);

    // 구군 선택 시 sgg_cd 하나 얻기
    String selectSggCdBySidoGugun(@Param("sido") String sido,
                                  @Param("gugun") String gugun);

    // 시도 선택 시 해당 시도에 속한 sgg_cd 리스트
    List<String> selectSggCdsBySido(@Param("sido") String sido);

    // 동 선택 시 dong_code 얻기(혹시 프론트가 동 이름만 주는 경우 대비)
    String selectDongCode(@Param("sido") String sido,
                          @Param("gugun") String gugun,
                          @Param("dong") String dong);
}
