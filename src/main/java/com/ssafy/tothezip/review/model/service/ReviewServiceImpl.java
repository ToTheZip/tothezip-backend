package com.ssafy.tothezip.review.model.service;

import com.ssafy.tothezip.review.model.ReviewDto;
import com.ssafy.tothezip.review.model.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDto.ReviewListResponse getReviews(String aptSeq, int limit, int offset) {
        if (aptSeq == null || aptSeq.isBlank()) {
            throw new IllegalArgumentException("aptSeq is required");
        }

        int safeLimit = (limit <= 0) ? 5 : Math.min(limit, 50); // 너무 큰 limit 방지
        int safeOffset = Math.max(offset, 0);

        int total = reviewMapper.countReviewsByAptSeq(aptSeq);

        List<ReviewDto.ReviewItem> list =
                reviewMapper.selectReviewsByAptSeq(aptSeq, safeLimit, safeOffset);

        ReviewDto.ReviewListResponse res = new ReviewDto.ReviewListResponse();
        res.setTotalCount(total);
        res.setReviews(list);
        res.setHasMore(safeOffset + list.size() < total);

        ReviewDto.ReviewStats stats = reviewMapper.selectReviewStatsByAptSeq(aptSeq);

        res.setAvgRating(stats.getAvgRating());
        res.setCount1(stats.getCount1());
        res.setCount2(stats.getCount2());
        res.setCount3(stats.getCount3());
        res.setCount4(stats.getCount4());
        res.setCount5(stats.getCount5());


        return res;
    }

    @Override
    public Integer createReview(int userId, ReviewDto.ReviewCreateRequest req) {
        if (req.getAptSeq() == null || req.getAptSeq().isBlank()) {
            throw new IllegalArgumentException("aptSeq is required");
        }
        if (req.getReviewContent() == null || req.getReviewContent().isBlank()) {
            throw new IllegalArgumentException("reviewContent is required");
        }
        int rating = (req.getReviewRating() == null) ? 0 : req.getReviewRating();
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("reviewRating must be 1~5");
        }

        reviewMapper.insertReview(req.getAptSeq(), userId, req.getReviewContent(), rating);
        return reviewMapper.selectLastInsertId(); // MySQL LAST_INSERT_ID()
    }

    @Override
    public void updateReview(int userId, int reviewId, ReviewDto.ReviewUpdateRequest req) {
        Integer owner = reviewMapper.selectReviewOwnerUserId(reviewId);
        if (owner == null) throw new IllegalArgumentException("review not found");
        if (owner != userId) throw new SecurityException("forbidden");

        String content = (req.getReviewContent() == null) ? "" : req.getReviewContent().trim();
        if (content.isBlank()) throw new IllegalArgumentException("reviewContent is required");

        int rating = (req.getReviewRating() == null) ? 0 : req.getReviewRating();
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("reviewRating must be 1~5");

        reviewMapper.updateReview(reviewId, content, rating);
    }

    @Override
    public void deleteReview(int userId, int reviewId) {
        Integer owner = reviewMapper.selectReviewOwnerUserId(reviewId);
        if (owner == null) throw new IllegalArgumentException("review not found");
        if (owner != userId) throw new SecurityException("forbidden");

        reviewMapper.deleteReview(reviewId);
    }

}
