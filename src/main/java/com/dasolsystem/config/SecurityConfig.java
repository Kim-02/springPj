package com.dasolsystem.config;

import com.dasolsystem.core.auth.userdetail.service.CustomUserDetailsService;
import com.dasolsystem.core.guardian.SecurityGuardian;
import com.dasolsystem.core.jwt.filter.JwtRequestFilter;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.handler.CustomAccessDeniedHandler;
import com.dasolsystem.core.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.SecurityContextDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomUserDetailsService userDetailsService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final SecurityGuardian securityGuardian;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/favicon.ico"
        );
    }


    @Bean
    public JwtRequestFilter jwtRequestFilter(RedisTemplate<Object,Object> redisTemplate, CustomUserDetailsService userDetailsService, SecurityGuardian securityGuardian) {
        return new JwtRequestFilter(redisTemplate,userDetailsService,securityGuardian);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtBuilder jwtBuilder) throws Exception {

        log.info("✅ securityFilterChain");
        //csrf, cors
        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        //세션 생성 안함
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //formLogin, BasicHttp 비활성화
        http.formLogin(form -> form.disable());
        http.httpBasic(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/",
                        "/api/auth/logout",
                        "/api/auth/login",
                        "/api/auth/signup",
                        "/api/ping",
                        "/index.html",
                        "/api/auth/verify",
                        "/api/auth/password/issuance",

                        "/swagger-ui/index.html",
                        "/swagger-ui/favicon-16x16.png",
                        "/swagger-ui/favicon-32x32.png",
                        "/swagger-ui/swagger-initializer.js",
                        "/swagger-ui/swagger-ui-standalone-preset.js",
                        "/swagger-ui/swagger-ui-bundle.js",
                        "/swagger-ui/index.css",
                        "/swagger-ui/swagger-ui.css",
                        "/v3/api-docs/swagger-config",
                        "/v3/api-docs"
                        ).permitAll()
                .anyRequest().authenticated()
        );

        //JWT필터 추가
        http.addFilterBefore(jwtRequestFilter(redisTemplate,userDetailsService,securityGuardian), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionhandling -> exceptionhandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));


        log.info("✅ 필터 설정 완료");
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://localhost:3000"));
        config.setAllowedOriginPatterns(List.of(
                "https://*.cloudfront.net",
                "https://kutcse.com"
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // 쿠키나 Authorization 헤더 허용
        config.setExposedHeaders(List.of("Authorization", "rAuthorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // /api/** 뿐만 아니라 Preflight 를 포함한 모든 경로에 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
