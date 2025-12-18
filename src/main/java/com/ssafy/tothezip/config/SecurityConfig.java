package com.ssafy.tothezip.config;

import com.ssafy.tothezip.security.CustomUserDetailService;
import com.ssafy.tothezip.security.JWTUtil;
import com.ssafy.tothezip.security.JWTVerificationFilter;
import com.ssafy.tothezip.security.SecurityExceptionHandlingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// BCrypt
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailsService;
    private final JWTUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                      @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig) throws Exception {

        var jwtVerifyFilter = new JWTVerificationFilter(jwtUtil, userDetailsService);
        var exceptionFilter = new SecurityExceptionHandlingFilter();

        http
                .securityMatcher("/**")

                .cors(cors -> cors.configurationSource(corsConfig))
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 비로그인 허용
                        .requestMatchers(HttpMethod.POST, "/user/regist").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/check-email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
                        // 그 외 /user/** 는 인증 필요
                        .requestMatchers("/user/email/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/*/preferences").permitAll()
                        // 공지 목록 허용
                        .requestMatchers(HttpMethod.GET, "/notice").permitAll()
                        .requestMatchers(HttpMethod.GET, "/notice/main").permitAll()
                        // 공지 상세 권한 설정
                        .requestMatchers(HttpMethod.GET, "/notice/*").authenticated()
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().authenticated()
                )

                // 필터 체인 순서: exceptionFilter → jwtVerifyFilter → UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JWTVerificationFilter.class);

        return http.build();
    }

    // 슬라이드의 corsConfigurationSource
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // /user/**에만 CORS 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
