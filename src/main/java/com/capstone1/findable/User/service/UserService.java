package com.capstone1.findable.User.service;

import com.capstone1.findable.Exception.ResourceConflictException;
import com.capstone1.findable.Exception.ResourceNotFoundException;
import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /** 회원 생성 */
    public void createUser(UserDTO.CreateUserDTO dto) {
        logger.info("☑️ [CREATE USER] email={}", dto.getEmail());
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            logger.error("❌ [CREATE USER] Email exists: {}", dto.getEmail());
            throw new ResourceConflictException("이미 등록된 이메일입니다.");
        }

        String encodedPwd = passwordEncoder.encode(dto.getPassword());
        User user = User.fromDTO(dto, encodedPwd);
        userRepo.save(user);
        logger.info("✅ [CREATE USER] 신규 사용자 저장 완료 email={}", dto.getEmail());
    }

    /** 사용자 엔티티 조회 */
    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다 id=" + id));
    }

    /** 전체 유저 조회 */
    @Transactional(readOnly = true)
    public List<UserDTO.ReadUserDTO> findAllUser() {
        logger.debug("☑️ [FIND ALL] Fetch all users");
        return userRepo.findAll().stream()
                .map(UserDTO.ReadUserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 이메일로 유저 조회 */
    @Transactional(readOnly = true)
    public UserDTO.ReadUserDTO findByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다 email=" + email));
        return UserDTO.ReadUserDTO.fromEntity(user);
    }

    /** ID로 유저 조회 */
    @Transactional(readOnly = true)
    public UserDTO.ReadUserDTO findUserById(Long id) {
        User user = getUserEntityById(id);
        return UserDTO.ReadUserDTO.fromEntity(user);
    }

    /** 유저 정보 업데이트 */
    public void updateUserInfo(Long id, UserDTO.ReadUserDTO dto) {
        logger.info("☑️ [UPDATE USER] id={}", id);
        User user = getUserEntityById(id);
        user.updateProfile(dto.getName(), dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.changePassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepo.save(user);
        logger.info("✅ [UPDATE USER] 수정 완료 id={}", id);
    }

    /** 유저 삭제 */
    public void deleteUser(Long id) {
        logger.info("☑️ [DELETE USER] id={}", id);
        User user = getUserEntityById(id);
        userRepo.delete(user);
        logger.info("✅ [DELETE USER] 삭제 완료 id={}", id);
    }
}