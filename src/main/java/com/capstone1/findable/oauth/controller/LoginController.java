package com.capstone1.findable.oauth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/loginForm")
    public String login(){
        return "loginForm";
    }

    @GetMapping("/test/oauth/login")
    public String testOauthLogin(Authentication authentication){ //DI로 PrincipalDetails를 받고, PrincipalDetails에는 User가 들어있다
        System.out.println("/test/oauth/login ===================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : " + oAuth2User.getAttributes()); //getAttributes -> user의 정보 Map<String,Object>로 받아온다

        return "Oauth Session Check";
    }
}