package com.capstone1.findable.oauth.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * "/" 접근 시,
     *  - Authentication이 존재하고 익명 사용자가 아니면 home.html로 리다이렉트
     *  - 아니면 login.html로 리다이렉트
     */
    @GetMapping("/")
    public String redirectRoot(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home.html";
        }
        return "redirect:/login.html";
    }
}
