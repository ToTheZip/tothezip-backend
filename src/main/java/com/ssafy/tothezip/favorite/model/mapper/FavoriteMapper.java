package com.ssafy.tothezip.favorite.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FavoriteMapper {

    int isLike(@Param("userId") Integer userId,
               @Param("type") String type,
               @Param("referenceId") Integer referenceId);

    int like(@Param("userId") Integer userId,
               @Param("type") String type,
               @Param("referenceId") Integer referenceId);

    int dislike(@Param("userId") Integer userId,
               @Param("type") String type,
               @Param("referenceId") Integer referenceId);
}
