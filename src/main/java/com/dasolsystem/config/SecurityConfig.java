package com.dasolsystem.config;

import com.dasolsystem.core.jwt.filter.JwtRequestFilter;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtBuilder jwtBuilder;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/", "/test/**", "/index.html"
                                        ,"/api/signin","/debug"
                                ).permitAll()
                                .requestMatchers("/api/**").hasRole("USER")
                                .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .accessDeniedHandler((req, res, ex) -> {              // ✅ 접근 거부 시 로그 확인
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.getWriter().write("❌ Access Denied: " + ex.getMessage());
                            log.error("❗ 접근 거부됨: {}", ex.getMessage());
                        })
                )
                //비밀번호 인증 전에 jwt 필터 적용
                .addFilterBefore(new JwtRequestFilter(jwtBuilder), UsernamePasswordAuthenticationFilter.class);
        log.info("✅ 필터 설정 완료");
        return http.build();
    }
}
