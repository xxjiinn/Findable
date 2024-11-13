package com.capstone1.findable;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "login";  // login.html 파일을 Thymeleaf를 통해 렌더링
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";  // signup.html 파일을 Thymeleaf를 통해 렌더링
    }

    @GetMapping("/home")
    public String home() {
        return "home"; // `home.html` 템플릿을 반환
    }

    @GetMapping("/post")
    public String postPage() {
        return "post";
    }
}
