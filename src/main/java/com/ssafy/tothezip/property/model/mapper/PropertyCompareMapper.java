package com.ssafy.tothezip.property.model.mapper;

import com.ssafy.tothezip.property.model.PropertyCompareDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PropertyCompareMapper {

    PropertyCompareDto.BaseListing findBaseWithCoord(@Param("propertyId") Integer propertyId);

    List<PropertyCompareDto.CandidateListing> findCandidatesMonthly(
            @Param("propertyId") Integer propertyId,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusM") Integer radiusM,
            @Param("depMin") Integer depMin, @Param("depMax") Integer depMax,
            @Param("rentMin") Integer rentMin, @Param("rentMax") Integer rentMax,
            @Param("areaMin") Double areaMin, @Param("areaMax") Double areaMax,
            @Param("limit") Integer limit
    );

    List<PropertyCompareDto.CandidateListing> findCandidatesSaleJeonse(
            @Param("propertyId") Integer propertyId,
            @Param("type") String type,
            @Param("baseLat") Double baseLat,
            @Param("baseLng") Double baseLng,
            @Param("radiusM") Integer radiusM,
            @Param("priceMin") Integer priceMin,
            @Param("priceMax") Integer priceMax,
            @Param("areaMin") Double areaMin,
            @Param("areaMax") Double areaMax,
            @Param("limit") Integer limit
    );

    Double findRatingByAptSeq(@Param("aptSeq") String aptSeq);

    List<PropertyCompareDto.PricePoint> findRecentPriceSeriesByAptSeq(
            @Param("aptSeq") String aptSeq,
            @Param("limit") int limit
    );

}
