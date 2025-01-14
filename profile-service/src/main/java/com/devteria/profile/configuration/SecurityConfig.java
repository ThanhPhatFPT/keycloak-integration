package com.devteria.profile.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {"/login", "/register","profiles"}; // Thêm /login vào danh sách endpoint công khai

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cho phép tất cả người dùng truy cập /login và /register mà không cần xác thực
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Cho phép truy cập miễn phí vào /login và /register
                .anyRequest().authenticated()); // Các yêu cầu khác sẽ yêu cầu xác thực

        // Cấu hình OAuth2 Resource Server với JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())); // Cấu hình EntryPoint cho JWT

        // Tắt CSRF nếu ứng dụng là API hoặc không yêu cầu bảo vệ CSRF
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}

