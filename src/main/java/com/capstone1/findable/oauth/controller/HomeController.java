package com.capstone1.findable.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToHome() {
        // 기본 페이지를 로그인 페이지로 리다이렉트
        return "redirect:/login.html";
    }
}
