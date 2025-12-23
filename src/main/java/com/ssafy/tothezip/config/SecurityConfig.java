package com.ssafy.tothezip.config;

import com.ssafy.tothezip.security.CustomUserDetailService;
import com.ssafy.tothezip.security.JWTUtil;
import com.ssafy.tothezip.security.JWTVerificationFilter;
import com.ssafy.tothezip.security.SecurityExceptionHandlingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    /**
     * 1. 세션 기반 체인: 이메일 인증/회원가입/프로필 업로드
     */
    @Bean
    @Order(1)
    public SecurityFilterChain sessionFlowChain(HttpSecurity http,
                                                CorsConfigurationSource corsConfig) throws Exception {

        http
                .securityMatcher(
                        "/user/email/**",
                        "/user/profile/upload",
                        "/user/regist"
                )
                .cors(cors -> cors.configurationSource(corsConfig))
                .csrf(csrf -> csrf.disable())
                // 세션 허용
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        // 이메일 인증/가입 플로우는 비로그인 허용
                        .requestMatchers(HttpMethod.POST, "/user/email/send-code").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/email/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/profile/upload").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/regist").permitAll()
                        .anyRequest().denyAll()
                );

        return http.build();
    }

    /**
     * 2. JWT 기반 체인: 나머지 API 전부 (stateless)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain jwtApiChain(HttpSecurity http,
                                           CorsConfigurationSource corsConfig) throws Exception {

        var jwtVerifyFilter = new JWTVerificationFilter(jwtUtil, userDetailsService);
        var exceptionFilter = new SecurityExceptionHandlingFilter();

        http
                .securityMatcher("/**")
                .cors(cors -> cors.configurationSource(corsConfig))
                .csrf(csrf -> csrf.disable())
                .userDetailsService(userDetailsService)
                // 나머지는 stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 비로그인 허용
                        .requestMatchers(HttpMethod.POST, "/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/logout").permitAll()

                        .requestMatchers(HttpMethod.GET, "/user/check-email").permitAll()

                        // 지역 선택 패널 API 허용 (시군구, 읍면동..)
                        .requestMatchers(HttpMethod.GET, "/regions/**").permitAll()
                        // 아파트명 자동완성 검색 API 허용
                        .requestMatchers(HttpMethod.GET, "/property/autocomplete").permitAll()
                        .requestMatchers(HttpMethod.POST, "/property/search").permitAll()

                        // 매물 리스트 불러오기 허용
                        .requestMatchers(HttpMethod.GET, "/property/*/listings").permitAll()

                        // 리뷰 조회 허용
                        .requestMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                        // 리뷰 작성은 로그인 필요
                        .requestMatchers(HttpMethod.POST, "/reviews/**").authenticated()

                        // 공지 목록 허용
                        .requestMatchers(HttpMethod.GET, "/notice").permitAll()
                        .requestMatchers(HttpMethod.GET, "/notice/main").permitAll()
                        .requestMatchers(HttpMethod.GET, "/notice/calendar").permitAll()

                        // 공지 상세는 인증 필요
                        .requestMatchers(HttpMethod.GET, "/notice/*").authenticated()

                        .requestMatchers("/admin/**").authenticated()

                        // 잠만
                        .requestMatchers("/property/recommendations").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/property/regions/sido",
                                "/property/regions/gugun",
                                "/property/tags"
                        ).permitAll()

                        .requestMatchers("/favorite/**").authenticated()


                        .anyRequest().authenticated()
                )
                // 필터 체인 순서
                .addFilterBefore(jwtVerifyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JWTVerificationFilter.class);

        return http.build();
    }

    /**
     * CORS
     * - 세션 쿠키(JSESSIONID) 쓰려면 allowCredentials(true) 필요
     * - allowedOrigins는 "*" 불가 (정확한 origin만)
     */
    @Bean
    @Primary
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
