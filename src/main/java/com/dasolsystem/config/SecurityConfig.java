package com.dasolsystem.config;

import com.dasolsystem.core.auth.userdetail.service.CustomUserDetailsService;
import com.dasolsystem.core.jwt.filter.JwtRequestFilter;
import com.dasolsystem.core.jwt.util.JwtBuilder;
import com.dasolsystem.core.handler.CustomAccessDeniedHandler;
import com.dasolsystem.core.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private final CustomUserDetailsService userDetailsService;
    private final JwtBuilder jwtBuilder;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/favicon.ico"
        );
    }


    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtBuilder jwtBuilder, CustomUserDetailsService userDetailsService) {
        return new JwtRequestFilter(jwtBuilder,userDetailsService);
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
                        "/api/auth/login",
                        "/api/auth/signup",
                        "/",
                        "/api/press",
                        "/api/likes",
                        "/test/**",

                        "/index.html",
                        "/deposit"

                        ,"/api/logout"
                        ,"/favicon.ico",
                        "/test/print",



                        "/api/users/upload",
                        "/api/amount/update",
                        "/api/users/personal_upload",
                        "/api/users/userdata",
                        "/api/users/deleteuser",
                        "/api/amount/download",
                        "/api/amount/userdata",
                        "/api/amount/personal/update",
                        "/api/users/updateuser",
                        "/api/users/personal_upload",
                        "/api/users/finduserid",
                        "/api/amount/refund",
                        "/api/expend/update",
                        "/check_amount/findexpender",

                        "/api/deposit/file/update",
                        "/api/deposit/personal/update",
                        "/api/amount/download/amount_check"
                        ).permitAll()
                .anyRequest().authenticated()
        );

        //JWT필터 추가
        http.addFilterBefore(jwtRequestFilter(jwtBuilder,userDetailsService), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exptionhandling -> exptionhandling
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));


        log.info("✅ 필터 설정 완료");
        return http.build();
    }
}
