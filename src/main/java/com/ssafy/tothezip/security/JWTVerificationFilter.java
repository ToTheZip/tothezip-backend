package com.ssafy.tothezip.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTVerificationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            // 토큰이 없으면 검증을 하지 않는다 -> 다음 필터로
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 토큰에서 claim 정보 획득
        Claims claims = jwtUtil.getClaims(token);

        String email = claims.get("email", String.class);

        // 2. 실제 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // 3. Authentication 생성 및 SecurityContextHolder에 저장
        var authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 다음 필터 호출
        filterChain.doFilter(request, response);
    }

    // 슬라이드 extractToken 메서드
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}
