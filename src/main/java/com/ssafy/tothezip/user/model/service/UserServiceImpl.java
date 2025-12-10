package com.ssafy.tothezip.user.model.service;

import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    @Override
    public void regist(UserDto userDto) {
        userMapper.regist(userDto);
    }

    @Override
    public boolean emailDuplicate(String email) {
        return userMapper.emailDuplicate(email);
    }

    @Override
    public UserDto login(String email, String password) {
        return userMapper.login(email, password);
    }

    @Override
    public UserDto getInfo(int userId) {
        return userMapper.getInfo(userId);
    }

    @Override
    public int update(UserDto userDto) {
       return userMapper.update(userDto);
    }

    @Override
    public void delete(int userId) {
        userMapper.delete(userId);
    }
}
