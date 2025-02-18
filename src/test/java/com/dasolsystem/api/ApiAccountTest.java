//package com.dasolsystem.api;
//
//import com.dasolsystem.core.tests.account.repository.AccountRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.ResponseEntity;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class ApiAccountTest {
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private AccountRepository accountRepository;
//    @Test
//    void TestAccountEndpointTest(){
//        String testName = "User_402";
//        String resMessage = "Test account #402";
//
//        String url = "http://localhost:" + port + "/account/api/test?name=" + testName;
//
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//
//        // 검증
//        assertThat(response.getStatusCodeValue()).isEqualTo(200); // HTTP 200 OK 확인
//        assertThat(response.getBody()).isEqualTo(resMessage); // 응답 메시지 확인
//    }
//
//    @Test
//    void testAccountEndpointTimeCheckNano() {
//        String testName = "User_402";
//
//        // 시작 시간 (나노초)
//        long startTime = System.nanoTime();
//
//        // 데이터 조회
//        accountRepository.findByName(testName);
//
//        // 종료 시간 (나노초)
//        long endTime = System.nanoTime();
//
//        // 실행 시간 출력 (나노초 단위)
//        System.out.println("Execution Time (ns): " + (endTime - startTime) + " ns");
//    }
//
//
//}
