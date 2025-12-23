package com.ssafy.tothezip.review.model.mapper;

import com.ssafy.tothezip.review.model.ReviewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    int countReviewsByAptSeq(@Param("aptSeq") String aptSeq);

    List<ReviewDto.ReviewItem> selectReviewsByAptSeq(
            @Param("aptSeq") String aptSeq,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
