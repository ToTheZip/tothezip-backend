package com.ssafy.tothezip.user.model.service;

import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.mapper.UserMapper;
import com.ssafy.tothezip.util.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    @Override
    public void regist(UserDto userDto) {
        String rawPassword = userDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userDto.setPassword(encodedPassword);

        userMapper.regist(userDto);
    }

    @Override
    public boolean emailDuplicate(String email) {
        return userMapper.emailDuplicate(email);
    }

    // 인증코드 생성 + 메일 발송
    @Override
    public String sendEmailCode(String email) {
        // 6자리 난수 생성
        String verifyCode = String.valueOf(new Random().nextInt(900_000) + 100_000);

        // 메일 발송 (전에 쓰던 MailUtil 그대로 사용한다고 가정)
        MailUtil.sendVerificationCode(email, verifyCode);

        return verifyCode;
    }

    // 사용자가 제출한 코드/이메일이 세션에 저장된 것과 일치하는지 확인
    @Override
    public boolean verifyEmailCode(String submittedCode,
                                   String submittedEmail,
                                   String sessionCode,
                                   String sessionEmail) {

        return sessionCode != null
                && sessionEmail != null
                && sessionCode.equals(submittedCode)
                && sessionEmail.equals(submittedEmail);
    }

//    @Override
//    public UserDto login(String email, String password) {
//        return userMapper.login(email, password);
//    }
    @Override
    public UserDto login(String email, String password){
        UserDto user = userMapper.findByEmail(email);
        if (user == null) {
            return null;
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            return null;
        }

        return user;
    }

    @Override
    public UserDto getInfo(int userId) {
        return userMapper.getInfo(userId);
    }

    @Override
    public int update(UserDto userDto) {
        // 비밀번호가 null/빈 문자열 아니면 변경 요청으로 보고 재해시
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            String encoded = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encoded);
        }
       return userMapper.update(userDto);
    }

    @Override
    public void delete(int userId) {
        userMapper.delete(userId);
    }
}
