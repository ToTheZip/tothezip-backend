package com.ssafy.tothezip.user.controller;

import com.ssafy.tothezip.security.CustomUserDetails;
import com.ssafy.tothezip.security.JWTUtil;
import com.ssafy.tothezip.user.model.*;
import com.ssafy.tothezip.user.model.service.ProfileImageService;
import com.ssafy.tothezip.user.model.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;
    private final JWTUtil jwtUtil;
    private final ProfileImageService profileImageService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,20}$");

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String pw) {
        return pw != null && PASSWORD_PATTERN.matcher(pw).matches();
    }

    // 회원가입 (이메일 인증 여부 확인)
    @PostMapping("/regist")
    public ResponseEntity<Map<String, Integer>> regist(@RequestBody UserDto userDto,
            HttpSession session) {
        log.debug("regist user: {}", userDto);

        if (!isValidEmail(userDto.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", 4001));
        }
        if (!isValidPassword(userDto.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", 4002));
        }

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

        // 3. 프로필 이미지 경로 검증
        if (userDto.getProfileImg() != null && !userDto.getProfileImg().isBlank()) {
            if (!userDto.getProfileImg().startsWith("/uploads/")) {
                return ResponseEntity.badRequest().build();
            }
        }

        // 4. 회원가입 진행
        UserDto user = userService.regist(userDto);

        // 5. 사용 후 세션 인증 정보 정리(선택)
        session.removeAttribute("emailVerificationCode");
        session.removeAttribute("emailVerificationEmail");
        session.removeAttribute("emailVerified");

        // return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("userId", user.getUserId()));
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
        System.out.println(code);

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
                sessionEmail);

        if (result) {
            // 인증 성공 → 세션에 flag 설정
            session.setAttribute("emailVerified", true);
        }

        return ResponseEntity.ok(result); // true = 인증성공, false = 실패
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<ProfileUploadResponseDto> uploadProfile(
            @RequestPart("file") MultipartFile file,
            HttpSession session) {
        // 이메일 인증된 세션만 업로드 허용 (스토리지 악용 방지)
        Boolean verified = (Boolean) session.getAttribute("emailVerified");
        if (verified == null || !verified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String url = profileImageService.uploadProfileImage(file);
        return ResponseEntity.ok(new ProfileUploadResponseDto(url));
    }

    // 로그인 + JWT 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDto loginRequest, HttpServletResponse response) {
        log.debug("login email: {}", loginRequest.getEmail());

        UserDto loginUser = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 비밀번호는 응답에서 제거
        loginUser.setPassword(null);

        String accessToken = jwtUtil.createAccessToken(loginUser);
        String refreshToken = jwtUtil.createRefreshToken(loginUser);

        // refreshToken -> HttpOnly Cookie
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // 로컬은 false, https 배포 시 true
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 14일
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok(new LoginResponseDto(accessToken, loginUser));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "REFRESH_TOKEN_MISSING"));
            }

            // 토큰 검증(만료/서명/타입)
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "REFRESH_TOKEN_INVALID"));
            }

            // refreshToken에서 userId/email만 꺼내기
            int userId = jwtUtil.getUserIdFromRefresh(refreshToken);

            // DB에서 사용자 정보 다시 조회해서 userName 확보
            UserDto user = userService.getInfo(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "USER_NOT_FOUND"));
            }
            user.setPassword(null);

            // 새 accessToken 발급
            String newAccessToken = jwtUtil.createAccessToken(user);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            log.error("refresh error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "REFRESH_INTERNAL_ERROR"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // 쿠키 삭제: Max-Age=0 + 같은 path로 내려줘야 브라우저에서 지워짐
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false) // 배포하면 true로
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(Map.of("message", "LOGOUT_OK"));
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

    // 관심사항 변경
    @PutMapping("/preferences")
    public ResponseEntity<Void> updatePreferences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PreferenceDto preferenceDto) {
        int userId = userDetails.getUser().getUserId();
        userService.updatePreferences(userId, preferenceDto);
        return ResponseEntity.ok().build();
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

    // 사용자 희망 층수, 평수 조회
    @GetMapping("/preferences/range")
    public ResponseEntity<PreferenceDto> getPreferencesRange(@AuthenticationPrincipal CustomUserDetails userDetails) {

        int userId = userDetails.getUser().getUserId();

        PreferenceDto range = userService.getPreferenceRange(userId);
        return ResponseEntity.ok(range);
    }

    // 계약서 인증
    @PostMapping("/certification")
    public ResponseEntity<Void> certificateProperty(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> request) {
        int userId = userDetails.getUser().getUserId();
        String aptSeq = request.get("aptSeq");

        System.out.println("userId: " + userId);
        System.out.println("aptSeq: " + aptSeq);

        if (aptSeq == null || aptSeq.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        userService.certificateProperty(userId, aptSeq);
        return ResponseEntity.ok().build();
    }

    // 계약 인증 여부 확인
    @GetMapping("/certification/check")
    public ResponseEntity<Boolean> checkCertification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String aptSeq) {
        int userId = userDetails.getUser().getUserId();
        boolean isVerified = userService.checkCertification(userId, aptSeq);
        return ResponseEntity.ok(isVerified);
    }
}
