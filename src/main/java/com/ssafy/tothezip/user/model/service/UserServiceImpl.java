package com.ssafy.tothezip.user.model.service;

import com.ssafy.tothezip.favorite.model.mapper.FavoriteMapper;
import com.ssafy.tothezip.user.model.PreferenceDto;
import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.mapper.UserMapper;
import com.ssafy.tothezip.util.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private FavoriteMapper favoriteMapper;
    private final MailUtil mailUtil;

    @Override
    public UserDto regist(UserDto userDto) {
        String rawPassword = userDto.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        userDto.setPassword(encodedPassword);

        userMapper.regist(userDto);
        return userDto;
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
        mailUtil.sendVerificationCode(email, verifyCode);

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

//        favoriteMapper.delete
//        userMapper.deleteUserTags(userId);
        userMapper.delete(userId);
    }

    @Override
    @Transactional
    public void savePreferences(int userId, PreferenceDto preferenceDto) {

//        userMapper.savePreferenceRange(userId, preferenceDto);
//
//        userMapper.deleteUserPreferences(userId);
//
//        if (preferenceDto != null &&
//                preferenceDto.getTagIds() != null &&
//                !preferenceDto.getTagIds().isEmpty()) {
//            userMapper.insertUserPreferences(userId, preferenceDto.getTagIds());
//        }
        // 지역 → tag_id 변환
        Integer regionTagId = userMapper.findRegionTagId(
                preferenceDto.getSido(),
                preferenceDto.getGugun()
        );

        // 기존 태그 삭제
        userMapper.deleteUserTags(userId);

        // 주변시설 태그 저장
        if (preferenceDto.getTagIds() != null) {
            for (Integer tagId : preferenceDto.getTagIds()) {
                userMapper.insertUserTag(userId, tagId);
            }
        }

        // 지역 태그 저장 (1개만)
        if (regionTagId != null) {
            userMapper.insertUserTag(userId, regionTagId);
        }

        // 희망 평수, 층수 저장
        userMapper.saveAreaRange(
                userId,
                preferenceDto.getMinArea(),
                preferenceDto.getMaxArea(),
                preferenceDto.getMinFloor(),
                preferenceDto.getMaxFloor()
        );
    }

    @Transactional
    @Override
    public void updatePreferences(int userId, PreferenceDto pref) {

        // 기존 관심 태그 전부 삭제
        userMapper.deleteUserTags(userId);

        // 지역 태그 처리 (sido + gugun → tag_id)
        if (pref.getSido() != null && pref.getGugun() != null) {
            Integer regionTagId =
                    userMapper.findRegionTagId(pref.getSido(), pref.getGugun());
            if (regionTagId != null) {
                userMapper.insertUserTag(userId, regionTagId);
            }
        }

        // 시설 태그 처리
        if (pref.getTagIds() != null && !pref.getTagIds().isEmpty()) {
            userMapper.insertUserPreferences(userId, pref.getTagIds());
        }

        // 희망 층 / 평수 upsert
        userMapper.saveAreaRange(
                userId,
                pref.getMinArea(),
                pref.getMaxArea(),
                pref.getMinFloor(),
                pref.getMaxFloor()
        );
    }

    @Override
    public List<Integer> getPreferences(int userId) {
        return userMapper.getUserPreferences(userId);
    }

    @Override
    public PreferenceDto getPreferenceRange(int userId) {
        return userMapper.getPreferenceRange(userId);
    }

    @Override
    public void certificateProperty(int userId, String aptSeq){ userMapper.certificateProperty(userId, aptSeq); }
}
