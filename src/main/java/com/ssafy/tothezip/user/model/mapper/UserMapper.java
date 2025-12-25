package com.ssafy.tothezip.user.model.mapper;

import com.ssafy.tothezip.user.model.PreferenceDto;
import org.apache.ibatis.annotations.Mapper;

import com.ssafy.tothezip.user.model.UserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    // 회원 가입
    void regist(UserDto userDto);

    // email 중복 확인
    boolean emailDuplicate(@Param("email") String email);

    // 로그인
//    UserDto login(@Param("email") String email, @Param("password") String password);
    UserDto findByEmail(@Param("email") String email);

    // 회원 정보 확인
    UserDto getInfo(@Param("userId") int userId);

    // 회원 정보 수정
    int update(UserDto userDto);

    // 회원 탈퇴
    void delete(@Param("userId") int userId);

    // 사용자 관심태그 전부 삭제
//    void deleteUserPreferences(@Param("userId") int userId);

    // 사용자 관심태그 여러 개 insert
    void insertUserPreferences(@Param("userId") int userId, @Param("tagIds") List<Integer> tagIds);

    // 사용자 관심태그 조회
    List<Integer> getUserPreferences(@Param("userId") int userId);

    // 희망 평수 저장
    void savePreferenceRange(@Param("userId") int userId, @Param("pref") PreferenceDto pref);

    // 희망 평수 조회
    PreferenceDto getPreferenceRange(@Param("userId") int userId);

    Integer findRegionTagId(@Param("sido") String sido, @Param("gugun") String gugun);

    void deleteUserTags(int userId);

    void insertUserTag(@Param("userId") int userId, @Param("tagId") int tagId);

    void saveAreaRange(@Param("userId") int userId, @Param("minArea") Integer minArea, @Param("maxArea") Integer maxArea, @Param("minFloor") Integer minFloor, @Param("maxFloor") Integer maxFloor);

    void certificateProperty(@Param("userId") int userId, @Param("aptSeq") String aptSeq);
}