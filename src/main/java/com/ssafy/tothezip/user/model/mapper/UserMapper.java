package com.ssafy.tothezip.user.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.tothezip.user.model.UserDto;

@Mapper
public interface UserMapper {

    // 회원 가입 (id 중복검사 할건지?)
    void regist(UserDto userDto);

    // 로그인
    UserDto login(String email,String password);

    // 회원 정보 확인
    UserDto getInfo(int userId);

    // 회원 정보 수정
    UserDto update(UserDto userDto);

    // 회원 탈퇴 (userId 아니면 email)
    void delete(int userId);

}