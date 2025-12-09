package com.ssafy.tothezip.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.tothezip.user.model.UserDto;

@Mapper
public interface UserMapper {

    // 회원 가입
    void regist(UserDto userDto);

    // email 중복 확인
    boolean emailDuplicate(String email);

    // 로그인
    UserDto login(String email,String password);

    // 회원 정보 확인
    UserDto getInfo(int userId);

    // 회원 정보 수정
    UserDto update(UserDto userDto);

    // 회원 탈퇴
    void delete(int userId);

}