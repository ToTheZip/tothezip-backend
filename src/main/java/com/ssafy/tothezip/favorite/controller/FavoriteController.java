package com.ssafy.tothezip.favorite.controller;

import com.ssafy.tothezip.favorite.model.FavoriteDto;
import com.ssafy.tothezip.favorite.model.FavoriteToggleDto;
import com.ssafy.tothezip.favorite.model.service.FavoriteService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
@AllArgsConstructor
@Slf4j
public class FavoriteController {

    private FavoriteService favoriteService;

    @GetMapping("/check")
    public FavoriteToggleDto check(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestParam String type,
                                   @RequestParam Integer referenceId) {
        Integer userId = userDetails.getUser().getUserId();
        return new FavoriteToggleDto(favoriteService.isLike(userId, type, referenceId));
    }

    @PostMapping
    public FavoriteToggleDto like(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestBody FavoriteDto req) {
        Integer userId = userDetails.getUser().getUserId();
        log.debug("type : {}", req.getType());
        log.debug("refid : {}", req.getReferenceId());
        boolean favorited = favoriteService.like(userId, req.getType(), req.getReferenceId());
        return new FavoriteToggleDto(favorited);
    }

    @DeleteMapping
    public FavoriteToggleDto dislike(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @RequestParam String type,
                                    @RequestParam Integer referenceId) {
        Integer userId = userDetails.getUser().getUserId();
        boolean favorited = favoriteService.dislike(userId, type, referenceId);
        return new FavoriteToggleDto(favorited);
    }
}
