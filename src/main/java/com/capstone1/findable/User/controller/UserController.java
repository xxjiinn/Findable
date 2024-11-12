package com.capstone1.findable.User.controller;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostMapping("/createUser")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO.CreateUserDTO dto) {
        try {
            userService.createUser(dto);
            logger.info("✅ 사용자 생성 성공!");
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("⚠️ 사용자 생성 실패 ㅠㅠ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO.ReadUserDTO>> findAllUser() {
        try {
            List<UserDTO.ReadUserDTO> users = userService.findAllUser();
            logger.info("✅ 모든 사용자 정보 조회!");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("⚠️ 사용자 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.ReadUserDTO> findUserById(@PathVariable Long id) {
        try {
            UserDTO.ReadUserDTO user = userService.findUserById(id);
            logger.info("✅ {}번 사용자 정보 조회!", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("⚠️ {}번 사용자 정보 조회 실패", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUserInfo(@PathVariable Long id, @RequestBody UserDTO.ReadUserDTO dto) {
        try {
            userService.updateUserInfo(id, dto);
            logger.info("✅ {}번 사용자 정보 수정!", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("⚠️ {}번 사용자 정보 수정 실패", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserInfo(@PathVariable Long id) {
        try {
            userService.deleteUserInfo(id);
            logger.info("✅ {}번 사용자 정보 삭제!", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("⚠️ {}번 사용자 정보 삭제 실패", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
