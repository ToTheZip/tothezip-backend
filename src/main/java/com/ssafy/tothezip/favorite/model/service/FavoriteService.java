package com.ssafy.tothezip.favorite.model.service;

public interface FavoriteService {

    boolean isLike(Integer userId, String type, Integer referenceId);
    boolean like(Integer userId, String type, Integer referenceId);
    boolean dislike(Integer userId, String type, Integer referenceId);
}
