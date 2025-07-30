package com.capstone1.findable.config;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticResourceController {

    /** favicon 및 appspecific 경로 요청을 204 응답 처리 */
    @RequestMapping("/favicon.ico")
    public ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
