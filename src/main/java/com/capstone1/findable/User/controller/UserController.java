package com.capstone1.findable.User.controller;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.service.UserService;
import com.capstone1.findable.config.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO.CreateUserDTO dto) {
        logger.info("🔥 [SIGNUP] Attempt with data: {}", dto);
        if (dto.getName() == null || dto.getEmail() == null || dto.getPassword() == null) {
            logger.error("❌ [SIGNUP] Missing required fields");
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.createUser(dto);
            logger.info("✅ [SIGNUP] User created successfully with email: {}", dto.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            logger.error("⚠️ [SIGNUP] User creation failed for email: {}", dto.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO.LoginUserDTO loginDTO, HttpServletResponse response) {
        logger.info("🔥 [LOGIN] Attempt with email: {}", loginDTO.getEmail());
        if (loginDTO.getEmail() == null || loginDTO.getPassword() == null) {
            logger.error("❌ [LOGIN] Missing required fields");
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
        }

        try {
            Map<String, String> tokens = userService.loginUser(loginDTO);

            addTokenToCookie(response, "accessToken", tokens.get("accessToken"), false, false);
            addTokenToCookie(response, "refreshToken", tokens.get("refreshToken"), true, true);

            logger.info("✅ [LOGIN] Successful for email: {}", loginDTO.getEmail());
            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (IllegalArgumentException e) {
            logger.error("⚠️ [LOGIN] Failed for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
    }

    private void addTokenToCookie(HttpServletResponse response, String name, String token, boolean httpOnly, boolean secure) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO.ReadUserDTO> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(UserDTO.ReadUserDTO.builder()
                .id(userDetails.getId())
                .name(userDetails.getUsername())
                .email(userDetails.getEmail())
                .build());
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO.ReadUserDTO>> findAllUser() {
        logger.info("🔥 [FIND ALL USERS]");
        List<UserDTO.ReadUserDTO> users = userService.findAllUser();
        logger.info("✅ [FIND ALL USERS] Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.ReadUserDTO> findUserById(@PathVariable Long id) {
        logger.info("🔥 [FIND USER BY ID] Fetching user with id: {}", id);
        UserDTO.ReadUserDTO user = userService.findUserById(id);
        logger.info("✅ [FIND USER BY ID] Fetched user with id: {}", id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO.ReadUserDTO dto) {
        logger.info("🔥 [UPDATE USER] Updating user with id: {}", id);
        userService.updateUserInfo(id, dto);
        logger.info("✅ [UPDATE USER] Updated user with id: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("🔥 [DELETE USER] Deleting user with id: {}", id);
        userService.deleteUser(id);
        logger.info("✅ [DELETE USER] Deleted user with id: {}", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
