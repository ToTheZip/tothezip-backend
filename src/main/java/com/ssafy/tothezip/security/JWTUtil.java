package com.ssafy.tothezip.security;

import com.ssafy.tothezip.user.model.UserDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JWTUtil {

    private SecretKey key;

    // 테스트용
    @Value("${ssafy.jwt.secret}")
    private String secret;
    // ---

    @Value("${ssafy.jwt.access-expmin}")
    private long accessExpMin;

    @Value("${ssafy.jwt.refresh-expmin}")
    private long refreshExpMin;

    @PostConstruct
    public void init() {
        // 슬라이드처럼 서버 기동 시 랜덤 key 생성
        // 이걸루 써야함
        // this.key = Jwts.SIG.HS256.key().build();
        // 테스트용
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // ---
    }

    // Access Token
    public String createAccessToken(UserDto user) {
        return create(
                "accessToken",
                accessExpMin,
                Map.of(
                        "userId", user.getUserId(),
                        "email", user.getEmail(),
                        "userName", user.getUserName()
                )
        );
    }

    // Refresh Token
    public String createRefreshToken(UserDto user) {
        return create(
                "refreshToken",
                refreshExpMin,
                Map.of(
                        "userId", user.getUserId(),
                        "email", user.getEmail()
                )
        );
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);

            // subject가 refreshToken인지 확인
            String subject = claims.getSubject();
            if (!"refreshToken".equals(subject)) return false;

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public int getUserIdFromRefresh(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        Integer userId = claims.get("userId", Integer.class);
        if (userId == null) throw new JwtException("NO_USER_ID");
        return userId;
    }

    // 공통 생성 로직 (슬라이드의 create 메서드)
    public String create(String subject, long expireMin, Map<String, Object> claims) {

        Date expireDate = new Date(System.currentTimeMillis() + 1000L * 60L * expireMin);

        String jwt = Jwts.builder()
                .subject(subject)       // 제목 (accessToken / refreshToken)
                .claims(claims)         // claim(key-value 쌍)
                .expiration(expireDate) // 만료일
                .signWith(key)          // 서명 알고리즘 설정
                .compact();             // 직렬화

        log.debug("토큰 발행: {}", jwt);
        return jwt;
    }

    // 슬라이드의 getClaims
    public Claims getClaims(String jwt) {
        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        Jws<Claims> jws = parser.parseSignedClaims(jwt);
        log.debug("claims : {}", jws);
        return jws.getPayload();
    }
}
