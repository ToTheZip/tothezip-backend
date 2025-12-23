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

    int insertReview(
            @Param("aptSeq") String aptSeq,
            @Param("userId") int userId,
            @Param("reviewContent") String reviewContent,
            @Param("reviewRating") int reviewRating
    );

    Integer selectLastInsertId();

    Integer selectReviewOwnerUserId(@Param("reviewId") int reviewId);

    int updateReview(
            @Param("reviewId") int reviewId,
            @Param("reviewContent") String reviewContent,
            @Param("reviewRating") int reviewRating
    );

    int deleteReview(@Param("reviewId") int reviewId);

    ReviewDto.ReviewStats selectReviewStatsByAptSeq(@Param("aptSeq") String aptSeq);

}
