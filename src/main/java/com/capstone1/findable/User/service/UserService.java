package com.capstone1.findable.User.service;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.Role;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import com.capstone1.findable.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 제공자
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // 회원 생성
    public void createUser(UserDTO.CreateUserDTO dto) {
        logger.info("➡️Creating new user with email: {}", dto.getEmail());

        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword()); // 비밀번호 암호화
        userRepo.save(User.builder()
                .username(dto.getName())
                .password(encodedPassword)
                .email(dto.getEmail())
                .role(Role.ROLE_USER) // 일반 사용자는 USER 권한 부여
                .build());

        logger.info("✅User created successfully with email: {}", dto.getEmail());
    } //

    // 로그인
    public String loginUser(UserDTO.LoginUserDTO loginDTO) {
        logger.info("☑️Login attempt for email: {}", loginDTO.getEmail());

        User user = userRepo.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("⚠️Login failed for email: {}. Invalid email or password.", loginDTO.getEmail());
                    return new IllegalArgumentException("Invalid email or password");
                });

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.error("⚠️Login failed for email: {}. Password mismatch.", loginDTO.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Access Token 및 Refresh Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        logger.info("🎟️ Access Token: {}", accessToken);
        logger.info("🎫 Refresh Token: {}", refreshToken);

        // 로그인 성공 시 JWT 생성
        // String token = jwtTokenProvider.generateAccessToken(user.getUsername());
        logger.info("✅Login successful for email: {}", loginDTO.getEmail());
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken).toString();
    }

    // 모든 유저 조회
    public List<UserDTO.ReadUserDTO> findAllUser() {
        logger.debug("➡️️Fetching all users");

        List<UserDTO.ReadUserDTO> users = userRepo.findAll()
                .stream()
                .map(UserDTO.ReadUserDTO::toDTO)
                .collect(Collectors.toList());

        logger.info("✅Found {} users", users.size());
        return users;
    }

    // ID로 유저 조회
    public UserDTO.ReadUserDTO findUserById(Long id) {
        logger.debug("➡️Fetching user by id: {}", id);

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
            logger.debug("➡️Updating user name to: {}", dto.getName());
            user.setUsername(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            logger.debug("➡️️Updating user email to: {}", dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        userRepo.save(user);
        logger.info("✅User updated successfully with id: {}", id);
    }

    // 유저 삭제
    public void deleteUser(Long id) {
        logger.info("️➡️Deleting user with id: {}", id);

        User user = userRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("⚠️No user found with id: {}", id);
                    return new IllegalStateException("No User.");
                });

        userRepo.delete(user);
        logger.info("✅User deleted successfully with id: {}", id);
    }
}
