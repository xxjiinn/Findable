package com.capstone1.findable.User.controller;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.service.UserService;
import com.capstone1.findable.config.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /** 회원 가입 */
    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDTO.CreateUserDTO dto) {
        logger.info("🔥 [SIGNUP] email={}", dto.getEmail());
        userService.createUser(dto);
        logger.info("✅ [SIGNUP] 회원 가입 완료 email={}", dto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 내 정보 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserDTO.ProfileDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("🔥 [ME] userId={}", userDetails.getId());
        UserDTO.ProfileDTO profile = UserDTO.ProfileDTO.fromEntity(
                userService.getUserEntityById(userDetails.getId())
        );
        return ResponseEntity.ok(profile);
    }

    /** 전체 유저 조회 */
    @GetMapping
    public ResponseEntity<List<UserDTO.ReadUserDTO>> findAllUser() {
        logger.info("🔥 [FIND ALL] 전체 유저 조회 시작");
        List<UserDTO.ReadUserDTO> users = userService.findAllUser();
        logger.info("✅ [FIND ALL] 조회 수={}", users.size());
        return ResponseEntity.ok(users);
    }

    /** 특정 유저 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.ReadUserDTO> findUserById(@PathVariable Long id) {
        logger.info("🔥 [FIND BY ID] userId={}", id);
        UserDTO.ReadUserDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    /** 유저 정보 수정 */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody UserDTO.ReadUserDTO dto) {
        logger.info("🔥 [UPDATE] userId={}", id);
        userService.updateUserInfo(id, dto);
        logger.info("✅ [UPDATE] 수정 완료 userId={}", id);
        return ResponseEntity.noContent().build();
    }

    /** 유저 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("🔥 [DELETE] userId={}", id);
        userService.deleteUser(id);
        logger.info("✅ [DELETE] 삭제 완료 userId={}", id);
        return ResponseEntity.noContent().build();
    }
}