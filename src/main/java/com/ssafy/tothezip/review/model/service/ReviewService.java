package com.ssafy.tothezip.review.model.service;

import com.ssafy.tothezip.review.model.ReviewDto;

public interface ReviewService {

    /**
     * 특정 aptSeq의 리뷰 목록 조회 (페이징)
     * @param aptSeq 아파트 식별자
     * @param limit  가져올 개수
     * @param offset 시작 위치
     */
    ReviewDto.ReviewListResponse getReviews(String aptSeq, int limit, int offset);

    Integer createReview(int userId, ReviewDto.ReviewCreateRequest req);

}
