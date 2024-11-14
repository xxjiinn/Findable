package com.capstone1.findable.User.service;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserDTO.CreateUserDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("⚠️ Email 중복!");
        }
        User user = User.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
    }


    @Transactional
    public List<UserDTO.ReadUserDTO> findAllUser() {
        return userRepo.findAll()
                .stream()
                .map(UserDTO.ReadUserDTO::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO.ReadUserDTO findUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while reading user info)"));
        return UserDTO.ReadUserDTO.toDTO(user);
    }

    @Transactional
    public void updateUserInfo(Long id, UserDTO.ReadUserDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while updating user info)"));
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        userRepo.save(user);
    }

    @Transactional
    public void deleteUserInfo(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while deleting user info)"));
        userRepo.delete(user);
    }
}
