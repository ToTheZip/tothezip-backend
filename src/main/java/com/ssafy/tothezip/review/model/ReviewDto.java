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

        private double avgRating;
        private int count1;
        private int count2;
        private int count3;
        private int count4;
        private int count5;
    }

    @Data
    public static class ReviewCreateRequest {
        private String aptSeq;
        private String reviewContent;
        private Integer reviewRating; // 1~5
    }

    @Data
    public static class ReviewCreateResponse {
        private Integer reviewId;
    }

    @Data
    public static class ReviewUpdateRequest {
        private String reviewContent;
        private Integer reviewRating; // 1~5
    }

    @Data
    public static class ReviewStats {
        private double avgRating;
        private int count1;
        private int count2;
        private int count3;
        private int count4;
        private int count5;
    }

}
