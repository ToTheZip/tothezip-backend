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


}
