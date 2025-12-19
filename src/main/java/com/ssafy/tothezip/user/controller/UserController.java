package com.ssafy.tothezip.user.controller;

import com.ssafy.tothezip.security.CustomUserDetails;
import com.ssafy.tothezip.security.JWTUtil;
import com.ssafy.tothezip.user.model.*;
import com.ssafy.tothezip.user.model.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;
    private final JWTUtil jwtUtil;

    // 회원가입 (이메일 인증 여부 확인)
    @PostMapping("/regist")
    public ResponseEntity<Void> regist(@RequestBody UserDto userDto,
                                       HttpSession session) {
        log.debug("regist user: {}", userDto);

        // 1. 이메일 중복 체크
        if (userService.emailDuplicate(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 2. 이메일 인증 여부 확인
        Boolean verified = (Boolean) session.getAttribute("emailVerified");
        String verifiedEmail = (String) session.getAttribute("emailVerificationEmail");

        if (verified == null || !verified || !userDto.getEmail().equals(verifiedEmail)) {
            // 인증되지 않았거나, 인증한 이메일과 다른 이메일로 가입 시도
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. 회원가입 진행
        userService.regist(userDto);

        // 4. 사용 후 세션 인증 정보 정리(선택)
        session.removeAttribute("emailVerificationCode");
        session.removeAttribute("emailVerificationEmail");
        session.removeAttribute("emailVerified");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailDuplicate(email);
        return ResponseEntity.ok(exists);
    }

    // 이메일로 인증코드 보내기
    @PostMapping("/email/send-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody EmailRequestDto request,
                                              HttpSession session) {

        String email = request.getEmail();
        log.debug("send-code email: {}", email);

        // 1. 이메일 중복 확인
        if (userService.emailDuplicate(email)) {
            // 이미 존재하는 이메일이면 메일 안 보내고 409로 응답
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 존재하는 이메일입니다."));
        }

        // 2. 인증코드 생성 + 메일 발송
        String code = userService.sendEmailCode(email);

        // 3. 세션에 코드와 이메일 저장
        session.setAttribute("emailVerificationCode", code);
        session.setAttribute("emailVerificationEmail", email);
        session.setAttribute("emailVerified", false);

        return ResponseEntity.ok(Map.of("message", "인증코드가 발송되었습니다."));
    }

    // 사용자가 입력한 코드 검증
    @PostMapping("/email/verify")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody EmailVerifyRequestDto request,
                                               HttpSession session) {

        String sessionCode = (String) session.getAttribute("emailVerificationCode");
        String sessionEmail = (String) session.getAttribute("emailVerificationEmail");

        boolean result = userService.verifyEmailCode(
                request.getCode(),
                request.getEmail(),
                sessionCode,
                sessionEmail
        );

        if (result) {
            // 인증 성공 → 세션에 flag 설정
            session.setAttribute("emailVerified", true);
        }

        return ResponseEntity.ok(result);   // true = 인증성공, false = 실패
    }

    // 로그인 + JWT 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto loginRequest) {
        log.debug("login email: {}", loginRequest.getEmail());

        UserDto loginUser =
                userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 비밀번호는 응답에서 제거
        loginUser.setPassword(null);

        String accessToken = jwtUtil.createAccessToken(loginUser);
        String refreshToken = jwtUtil.createRefreshToken(loginUser);

        LoginResponseDto body = new LoginResponseDto(accessToken, refreshToken, loginUser);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)
                .body(body);
    }

    // 회원 정보 조회
    @GetMapping
    public ResponseEntity<UserDto> getInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {

        int userId = userDetails.getUser().getUserId();

        UserDto user = userService.getInfo(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // 회원 정보 수정
    @PutMapping
    public ResponseEntity<UserDto> update(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @RequestBody UserDto userDto) {

        int userId = userDetails.getUser().getUserId();

        userDto.setUserId(userId);

        int updated = userService.update(userDto);
        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }

        UserDto updatedUser = userService.getInfo(userId);
        if (updatedUser != null) {
            updatedUser.setPassword(null);
        }
        return ResponseEntity.ok(updatedUser);
    }

    // 회원 탈퇴
    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails) {

        int userId = userDetails.getUser().getUserId();

        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // 사용자 관심태그 및 희망 가격, 평수 저장
    @PostMapping("/preferences")
    public ResponseEntity<Void> savePreferences(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestBody PreferenceDto preferenceDto) {

        int userId = userDetails.getUser().getUserId();

        userService.savePreferences(userId, preferenceDto);
        return ResponseEntity.ok().build();
    }

    // 사용자 관심태그 조회
    @GetMapping("/preferences/tags")
    public ResponseEntity<List<Integer>> getPreferences(@AuthenticationPrincipal CustomUserDetails userDetails) {

        int userId = userDetails.getUser().getUserId();

        List<Integer> tagIds = userService.getPreferences(userId);
        return ResponseEntity.ok(tagIds);
    }

    // 사용자 희망 가격, 평수 조회
    @GetMapping("/preferences/range")
    public ResponseEntity<PreferenceDto> getPreferencesRange(@AuthenticationPrincipal CustomUserDetails userDetails) {

        int userId = userDetails.getUser().getUserId();

        PreferenceDto range = userService.getPreferenceRange(userId);
        return ResponseEntity.ok(range);
    }
}
