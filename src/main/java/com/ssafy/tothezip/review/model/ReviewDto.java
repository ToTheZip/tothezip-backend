package com.ssafy.tothezip.review.model;

import lombok.Data;

import java.time.LocalDateTime;

public class ReviewDto {

    @Data
    public static class ReviewItem {
        private Integer reviewId;
        private String aptSeq;
        private Integer userId;

        private String profileImg;
        private String reviewContent;
        private Integer reviewRating;
        private LocalDateTime reviewDate;
    }

    @Data
    public static class ReviewListResponse {
        private int totalCount;
        private boolean hasMore;
        private java.util.List<ReviewItem> reviews;
    }
}
