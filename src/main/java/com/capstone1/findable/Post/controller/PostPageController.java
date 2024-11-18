package com.capstone1.findable.Post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/post")
public class PostPageController {

    @GetMapping("/create")
    public String createPostPage() {
        return "create_post"; // create_post.html 반환
    }

    @GetMapping("/list")
    public String listPostPage() {
        return "list_post"; // list_post.html 반환
    }

    @GetMapping("/search")
    public String searchPostPage() {
        return "search_post"; // search_post.html 반환
    }
}
