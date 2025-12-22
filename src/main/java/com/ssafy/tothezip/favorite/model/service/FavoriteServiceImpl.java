package com.ssafy.tothezip.favorite.model.service;

import com.ssafy.tothezip.favorite.model.mapper.FavoriteMapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private FavoriteMapper favoriteMapper;

    @Override
    public boolean isLike(Integer userId, String type, Integer referenceId) {
        return favoriteMapper.isLike(userId, type, referenceId) > 0;
    }

    @Override
    @Transactional
    public boolean like(Integer userId, String type, Integer referenceId) {
        try {
            favoriteMapper.like(userId, type, referenceId);
        } catch (DuplicateKeyException e) {
        }
        return true;
    }

    @Override
    @Transactional
    public boolean dislike(Integer userId, String type, Integer referenceId) {
        favoriteMapper.dislike(userId, type, referenceId);
        return false;
    }

    @Override
    public List<Integer> getFavoriteReferenceIds(Integer userId, String type) {
        return favoriteMapper.findReferenceIds(userId, type);
    }
}
