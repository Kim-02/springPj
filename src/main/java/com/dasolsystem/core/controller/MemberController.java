package com.dasolsystem.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {
    // 회원가입 페이지 출력 요청
    @GetMapping("/member/save")
    public String saveForm() {
        return "save"; // string이 templates 폴더 안에 있는 save.html을 찾아서 반환해주는 것
    }

    @PostMapping("/member/save")
    /* @RequestParam("memberEmail")에서 memberEmail은 매개변수로 생각 -> 이 값을 새로운 변수에 넣는 것*/
    public String save(@RequestParam("memberEmail") String memberEmail,
                       @RequestParam("memberPassword") String memberPassword,
                       @RequestParam("memberName") String memberName) {
        System.out.println("MemberController.save"); /* sout은 출력코드를 빠르게 만들어줌 */
        System.out.println("memberEmail = " + memberEmail + ", memberPassword = " + memberPassword + ", memberName = " + memberName);
        /* soutp는 매개변수를 기준으로 자동완성해줌 */
        return "dasoltest";
    }
}
