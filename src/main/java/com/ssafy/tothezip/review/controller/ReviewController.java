package com.ssafy.tothezip.review.controller;

import com.ssafy.tothezip.review.model.ReviewDto;
import com.ssafy.tothezip.review.model.service.ReviewService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{aptSeq}")
    public ReviewDto.ReviewListResponse getReviews(
            @PathVariable String aptSeq,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return reviewService.getReviews(aptSeq, limit, offset);
    }

    @PostMapping
    public ReviewDto.ReviewCreateResponse createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ReviewDto.ReviewCreateRequest req
    ) {
        int userId = userDetails.getUser().getUserId();
        Integer id = reviewService.createReview(userId, req);
        ReviewDto.ReviewCreateResponse res = new ReviewDto.ReviewCreateResponse();
        res.setReviewId(id);
        return res;
    }


}
