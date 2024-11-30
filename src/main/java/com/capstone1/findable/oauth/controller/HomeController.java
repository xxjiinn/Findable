package com.capstone1.findable.oauth.controller;

import com.capstone1.findable.oauth.service.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToAppropriatePage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login.html"; // 인증되지 않은 사용자는 로그인 페이지로 이동
        }

        // 사용자가 등록되지 않은 경우 회원가입 페이지로 이동
        if (!isUserRegistered(authentication)) {
            return "redirect:/signup.html";
        }

        // 인증된 사용자는 홈 페이지로 이동
        return "redirect:/home.html";
    }

    private boolean isUserRegistered(Authentication authentication) {
        if (authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
            return userDetails.isRegistered();
        }
        return false;
    }
}
