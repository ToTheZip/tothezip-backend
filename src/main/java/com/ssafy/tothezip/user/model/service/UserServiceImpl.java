package com.ssafy.tothezip.user.model.service;

import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
