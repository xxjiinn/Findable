package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.oauth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToAppropriatePage(Authentication authentication) {
        if (authentication == null) {
            // 로그인 안 되어 있을 경우 로그인 페이지로 리다이렉트
            return "redirect:/login.html";
        }

        // 로그인이 되어 있지만 회원가입이 안 되어 있을 경우
        if (!isUserRegistered(authentication)) {
            return "redirect:/signup.html";
        }

        // 로그인 되어 있을 경우 홈 페이지로 리다이렉트
        return "redirect:/home.html";
    }

    private boolean isUserRegistered(Authentication authentication) {
        // PrincipalDetails에서 회원가입 여부 확인
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        return userDetails.isRegistered(); // 회원가입 여부 반환
    }
}
