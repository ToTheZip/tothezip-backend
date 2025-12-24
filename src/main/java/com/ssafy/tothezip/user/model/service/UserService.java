package com.ssafy.tothezip.user.model.service;

import com.ssafy.tothezip.user.model.PreferenceDto;
import com.ssafy.tothezip.user.model.UserDto;

import java.util.List;

public interface UserService {

    // 회원 가입
    UserDto regist(UserDto userDto);

    // email 중복 확인
    boolean emailDuplicate(String email);

    // email 인증
    String sendEmailCode(String email);
    boolean verifyEmailCode(String submittedCode, String submittedEmail, String sessionCode, String sessionEmail);

    // 로그인
    UserDto login(String email,String password);

    // 회원 정보 확인
    UserDto getInfo(int userId);

    // 회원 정보 수정
    int update(UserDto userDto);

    // 회원 탈퇴
    void delete(int userId);

    // 관심태그 및 희망 평수 저장
    void savePreferences(int userId, PreferenceDto preferenceDto);

    void updatePreferences(int userId, PreferenceDto pref);

    // 관심태그 조회
    List<Integer> getPreferences(int userId);

    // 희망 평수 조회
    PreferenceDto getPreferenceRange(int userId);

    // 계약 인증
    void certificateProperty(int userId, String aptSeq);
}