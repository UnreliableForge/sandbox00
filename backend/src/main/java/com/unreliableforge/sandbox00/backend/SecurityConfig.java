package com.unreliableforge.sandbox00.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.unreliableforge.sandbox00.backend.filter.SessionValidationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SessionValidationFilter sessionValidationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        // ここではパスでの評価しかされない。
                        // セッションとユーザーでの評価はSessionValidationFilterで行う必要がある。
                        // .requestMatchers("/api/v1/hello").permitAll()
                        .requestMatchers("/api/v1/health").permitAll()
                        .requestMatchers("/api/v1/user/session").permitAll()
                        // .requestMatchers("/api/v1/user/whoami").permitAll()
                        .anyRequest().authenticated())
                // Spring Security の標準認証 Filter の前に自作 Filter を挟む
                // UsernamePasswordAuthenticationFilterは使わないが、だいたいこのへんにFilterいれとけばいいんじゃね。という目印的な感じ.
                .addFilterBefore(sessionValidationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
