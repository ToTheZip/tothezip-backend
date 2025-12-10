package com.ssafy.tothezip.user.controller;

import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<UserDto> regist(@RequestBody UserDto userDto) {
        log.debug("regist user: {}", userDto);

        if (userService.emailDuplicate(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        userService.regist(userDto);

        userDto.setPassword(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailDuplicate(email);
        return ResponseEntity.ok(exists);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto loginRequest) {
        log.debug("login email: {}", loginRequest.getEmail());

        UserDto loginUser = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        loginUser.setPassword(null);
        return ResponseEntity.ok(loginUser);
    }

    // 회원 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getInfo(@PathVariable int userId) {
        UserDto user = userService.getInfo(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
