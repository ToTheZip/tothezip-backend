package com.ssafy.tothezip.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.tothezip.user.model.UserDto;
import org.apache.ibatis.annotations.Param;

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

}