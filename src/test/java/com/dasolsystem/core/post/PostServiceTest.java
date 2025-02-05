//package com.dasolsystem.core.post;
//
//import com.dasolsystem.core.auth.repository.authRepository;
//import com.dasolsystem.core.auth.signup.controller.SignUpController;
//import com.dasolsystem.core.auth.signup.dto.RequestSignupPostDto;
//import com.dasolsystem.core.auth.signup.service.signupService;
//import com.dasolsystem.core.jwt.util.JwtBuilder;
//import com.dasolsystem.core.jwt.util.JwtBuilderImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class PostServiceTest {
//
//    @Autowired
//    private authRepository authRepository;
//
//    @Autowired
//    private signupService signupService;
//
//    @Test
//    @DisplayName("회원 추가")
//    void addSignUpTest() {
//        if(authRepository.findByEmailID("test")==null){
//            RequestSignupPostDto requestDto = RequestSignupPostDto.builder()
//                    .email("test")
//                    .password("1234")
//                    .userName("testUser")
//                    .build();
//
//            signupService.signup(requestDto);
//        }
//
//        System.out.println(authRepository.findByEmailID("1234"));
//    }
//
//}