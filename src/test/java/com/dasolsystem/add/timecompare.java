//package com.dasolsystem.add;
//
//import com.dasolsystem.core.tests.like.service.LikeService;
//import com.dasolsystem.core.tests.like.service.RedissonLockStockFacade;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class timecompare {
//
//    @Autowired
//    private LikeService likeService;
//
//    @Autowired
//    private RedissonLockStockFacade lockStockFacade;
//
//    @BeforeEach
//    public void setUp() {
//        // 필요한 초기화 작업이 있다면 여기에 추가
//    }
//
//    @Test
//    public void testMakeLikesExecutionTime() throws InterruptedException {
//        long startTime = System.currentTimeMillis();
//        likeService.makeLikes();
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//        System.out.println("makeLikes 실행 시간: " + duration + " ms");
//    }
//
//    @Test
//    public void testMakeLikeExecutionTime() {
//        long startTime = System.currentTimeMillis();
//        lockStockFacade.makeLike();
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//        System.out.println("makeLike 실행 시간: " + duration + " ms");
//    }
//}
