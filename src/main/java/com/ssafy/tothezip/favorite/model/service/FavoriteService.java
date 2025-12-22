package com.ssafy.tothezip.favorite.model.service;

import java.util.List;

public interface FavoriteService {

    boolean isLike(Integer userId, String type, Integer referenceId);
    boolean like(Integer userId, String type, Integer referenceId);
    boolean dislike(Integer userId, String type, Integer referenceId);
    List<Integer> getFavoriteReferenceIds(Integer userId, String type);
}
