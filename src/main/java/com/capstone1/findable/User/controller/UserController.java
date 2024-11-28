package com.capstone1.findable.User.controller;

import com.capstone1.findable.Config.CustomUserDetails;
import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.service.UserService;
import com.capstone1.findable.jwt.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 회원가입 엔드포인트
    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO.CreateUserDTO dto) {
        logger.info("➡️ User sign-up attempt with name: {}, email: {}", dto.getName(), dto.getEmail());
        try {
            userService.createUser(dto);
            logger.info("✅ User created successfully with email: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("⚠️ User creation failed for email: {}", dto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //

    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO.LoginUserDTO loginDTO) {
        logger.info("➡️ Login attempt with email: {}", loginDTO.getEmail());
        try {
            String accessToken = userService.loginUser(loginDTO); // Access Token 생성
            String refreshToken = jwtTokenProvider.generateRefreshToken(loginDTO.getEmail()); // Refresh Token 생성
            logger.info("✅ Login successful for email: {}", loginDTO.getEmail());
            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            )); // Access Token과 Refresh Token을 JSON으로 반환
        } catch (IllegalArgumentException e) {
            logger.error("⚠️ Login failed for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }


    // 현재 로그인된 사용자 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<UserDTO.ReadUserDTO> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(UserDTO.ReadUserDTO.builder()
                .id(userDetails.getId())
                .name(userDetails.getUsername())
                .email(userDetails.getEmail())
                .build());
    }

    // 모든 사용자 조회
    @GetMapping("")
    public ResponseEntity<List<UserDTO.ReadUserDTO>> findAllUser() {
        logger.info("➡️ Fetching all users");
        List<UserDTO.ReadUserDTO> users = userService.findAllUser();
        logger.info("✅ Fetched {} users", users.size());
        return ResponseEntity.ok(users);
    }

    // ID로 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.ReadUserDTO> findUserById(@PathVariable Long id) {
        logger.info("➡️ Fetching user with id: {}", id);
        UserDTO.ReadUserDTO user = userService.findUserById(id);
        logger.info("✅ Fetched user with id: {}", id);
        return ResponseEntity.ok(user); // 200 OK // // Security 공부 중....
    }

    // 사용자 정보 업데이트 /
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO.ReadUserDTO dto) {
        logger.info("➡️ Updating user with id: {}", id);
        userService.updateUserInfo(id, dto);
        logger.info("✅ Updated user with id: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("➡️ Deleting user with id: {}", id);
        userService.deleteUser(id);
        logger.info("✅ Deleted user with id: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
