//package com.capstone1.findable.oauth.controller;
//
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/redis-test")
//public class RedisTestController {
//    private final StringRedisTemplate stringRedisTemplate;
//
//    public RedisTestController(StringRedisTemplate stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//    }
//
//    @GetMapping("/set")
//    public String setValue() {
//        stringRedisTemplate.opsForValue().set("testKey", "Hello, Redis!");
//        return "Value set in Redis!";
//    }
//
//    @GetMapping("/get")
//    public String getValue() {
//        return stringRedisTemplate.opsForValue().get("testKey");
//    }
//}
//
