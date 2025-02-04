//package com.dasolsystem.api;
//
//
//import com.dasolsystem.core.jwt.dto.ResponsesignInJwtDto;
//import com.dasolsystem.core.jwt.dto.signInJwtBuilderDto;
//import com.dasolsystem.core.jwt.util.JwtBuilder;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class apiTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private JwtBuilder jwtBuilder;
//
//    private String getBaseUrl() {
//        return "http://localhost:" + port + "/api/secure";
//    }
//
//    @Test
//    @DisplayName("회원 로그인 -jwt발급")
//    void login(){
//        Long refreshId = jwtBuilder.getRefreshTokenId("testUser");
//    }
//    @DisplayName("정상적인 토큰 발급 테스트")
//    @Test
//    public void TestValidToken(){
//        String token = jwtBuilder.generateAccessToken("testUser");
//        String token2 = jwtBuilder.getRefreshTokenId("testUser");
//        String username = "testUser";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        headers.set("rAuthorization", "Bearer " + token2);
//        headers.set("USER-NAME", username);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isEqualTo("secure point");
//    }
//
//    @DisplayName("잘못된 토큰 테스트")
//    @Test
//    public void TestInvalidToken(){
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer invalid");
//        headers.set("rAuthorization","Bearer invalid");
//        headers.set("User-Name","testUser");
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//        assertThat(response.getStatusCodeValue()).isEqualTo(401);
//        assertThat(response.getBody()).isEqualTo("error.Wrong Token");
//    }
//
//    @DisplayName("access토큰 재발급 테스트")
//    @Test
//    public void TestAccessToken(){
//        String atoken = jwtBuilder.generateJWT("testUser",0L); //유효기간이 지난 토큰
//        String refreshToken = jwtBuilder.getRefreshTokenId("testUser");
//        jwtBuilder.saveRefreshToken(signInJwtBuilderDto.builder() //DB에 이름을 키로 한 리프레시 토큰 저장
//                .userName("testUser")
//                .rtoken(refreshToken).build());
//        HttpHeaders headers = new HttpHeaders();
//        //유효기간 만료 토큰
//        headers.set("Authorization", "Bearer "+ atoken);
//        headers.set("rAuthorization","Bearer "+ refreshToken);
//        headers.set("User-Name","testUser");
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isEqualTo("secure point");
//    }
//
//    @DisplayName("refresh 토큰 만료 테스트")
//    @Test
//    void TestInvaildRefreshToken(){
//        String atoken = jwtBuilder.generateJWT("testUser",0L);
//        String rtoken = jwtBuilder.generateJWT("testUser",0L);
//        jwtBuilder.saveRefreshToken(signInJwtBuilderDto.builder() //DB에 이름을 키로 한 리프레시 토큰 저장
//                .userName("testUser")
//                .rtoken(rtoken).build());
//        HttpHeaders headers = new HttpHeaders();
//        //유효기간 만료 토큰
//        headers.set("Authorization", "Bearer "+ atoken);
//        //유효기간 만료 토큰
//        headers.set("rAuthorization","Bearer "+ rtoken);
//        headers.set("User-Name","testUser");
//        ResponseEntity<String> response = restTemplate.exchange(
//                getBaseUrl(),
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//        assertThat(response.getStatusCodeValue()).isEqualTo(401);
//        assertThat(response.getBody()).isEqualTo("error.refresh token Expired");
//    }
//}
