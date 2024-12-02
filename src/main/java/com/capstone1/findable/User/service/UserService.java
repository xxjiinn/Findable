package com.capstone1.findable.User.service;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import com.capstone1.findable.oauth.entity.RefreshToken;
import com.capstone1.findable.oauth.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 제공자
    private final RefreshTokenRepo refreshTokenRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public void createUser(UserDTO.CreateUserDTO dto) {
        logger.info("☑️ [CREATE USER] Creating user with email: {}", dto.getEmail());
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("❌ [CREATE USER] Email already exists: {}", dto.getEmail());
            throw new IllegalArgumentException("Email already in use.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        userRepo.save(User.builder()
                .username(dto.getName())  // Username에 Name 저장
                .password(encodedPassword)
                .email(dto.getEmail())
                .role(Role.ROLE_USER)
                .registered(true) // 회원가입 상태
                .build());
        logger.info("✅ [CREATE USER] User created successfully with email: {}", dto.getEmail());
    }


    public Map<String, String> loginUser(UserDTO.LoginUserDTO loginDTO) {
        logger.info("☑️ [LOGIN] Attempt for email: {}", loginDTO.getEmail());

        // 이메일로 사용자 조회
        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("⚠️ [LOGIN] Failed. Invalid email: {}", loginDTO.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.error("⚠️ [LOGIN] Failed. Password mismatch for email: {}", loginDTO.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        logger.debug("🎟️ Access Token generated: {}", accessToken);

        // Refresh Token 생성 및 저장
        String refreshTokenValue = saveOrUpdateRefreshToken(user);
        logger.debug("🔑 Refresh Token generated: {}", refreshTokenValue);

        logger.info("✅ [LOGIN] Successful for email: {}", loginDTO.getEmail());
        return Map.of("accessToken", accessToken, "refreshToken", refreshTokenValue); // 두 토큰 반환
    }


    public String getRefreshTokenForUser(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("User not found for email: " + email));
        return saveOrUpdateRefreshToken(user);
    }


    private String saveOrUpdateRefreshToken(User user) {
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepo.findByUserId(user.getId());
        String refreshTokenValue;

        if (existingTokenOpt.isPresent() && existingTokenOpt.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            refreshTokenValue = existingTokenOpt.get().getToken();
            logger.debug("♻️ [REFRESH TOKEN] Existing token found for user: {}", user.getEmail());
        } else {
            refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());
            logger.debug("🔑 [REFRESH TOKEN] New token generated: {}", refreshTokenValue);

            refreshTokenRepo.save(RefreshToken.builder()
                    .token(refreshTokenValue)
                    .user(user)
                    .deviceId("Default_Device")
                    .expiryDate(LocalDateTime.now().plusWeeks(1))
                    .createdAt(LocalDateTime.now())
                    .build());
            logger.info("✅ [REFRESH TOKEN] Saved to DB for user: {}", user.getEmail());
        }

        return refreshTokenValue;
    }

    // 모든 유저 조회
    public List<UserDTO.ReadUserDTO> findAllUser() {
        logger.debug("☑️Fetching all users");

        List<UserDTO.ReadUserDTO> users = userRepo.findAll()
                .stream()
                .map(UserDTO.ReadUserDTO::toDTO)
                .collect(Collectors.toList());

        logger.info("✅Found {} users", users.size());
        return users;
    }

    // ID로 유저 조회
    public UserDTO.ReadUserDTO findUserById(Long id) {
        logger.debug("☑️Fetching user by id: {}", id);

        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("⚠️No user found with id: {}", id);
                    return new IllegalArgumentException("No User Found!!!");
                });

        logger.info("✅User found with id: {}", id);
        return UserDTO.ReadUserDTO.toDTO(user);
    }

    // 유저 정보 업데이트
    public void updateUserInfo(Long id, UserDTO.ReadUserDTO dto) {
        logger.info("☑️Updating user with id: {}", id);

        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("⚠️No user found with id: {}", id);
                    return new IllegalStateException("No User.");
                });

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            logger.debug("☑️Updating user name to: {}", dto.getName());
            user.setUsername(dto.getName());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            logger.debug("☑️Updating user password.");
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            logger.debug("☑️Updating user email to: {}", dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        userRepo.save(user);
        logger.info("✅User updated successfully with id: {}", id);
    }

    // 유저 삭제
    public void deleteUser(Long id) {
        logger.info("☑️Deleting user with id: {}", id);

        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("⚠️No user found with id: {}", id);
                    return new IllegalStateException("No User.");
                });

        userRepo.delete(user);
        logger.info("✅User deleted successfully with id: {}", id);
    }
}
