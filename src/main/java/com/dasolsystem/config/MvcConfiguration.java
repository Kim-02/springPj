package com.dasolsystem.config;


import com.dasolsystem.core.jwt.filter.JwtRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

//    @Bean
////    public FilterRegistrationBean<JwtRequestFilter> jwtFilter(JwtRequestFilter jwtRequestFilter) {
////        FilterRegistrationBean<JwtRequestFilter> registrationBean = new FilterRegistrationBean<>();
////        registrationBean.setFilter(jwtRequestFilter);
////        registrationBean.addUrlPatterns("/api/*"); // 적용할 URL 패턴 확인
////        return registrationBean;
////    }
}
