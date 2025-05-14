package com.lazycode.lazyhotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/auth/**").permitAll()       // ✅ Public
                .requestMatchers("/rooms/**").permitAll()      // ✅ Public
                .requestMatchers("/admin/**").authenticated()  // 🔐 Yêu cầu xác thực
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Cho phép preflight request
                .anyRequest().permitAll() // ✅ Những route khác cũng public (nếu muốn)
            .and()
            .formLogin().disable()
            .httpBasic().disable();

        return http.build();
    }
}

