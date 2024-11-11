package com.capstone1.findable.User.service;

import com.capstone1.findable.User.dto.UserDTO;
import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void createUser (UserDTO.CreateUserDTO dto){
        userRepo.save(User.toEntity(dto));
    }

    public List<UserDTO.ReadUserDTO> findAllUser(){
        return userRepo.findAll()
                .stream()
                .map(UserDTO.ReadUserDTO::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO.ReadUserDTO findUserById(Long id){
        User user =  userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while reading user info)"));
        return UserDTO.ReadUserDTO.toDTO(user);
    }

    public void updateUserInfo(Long id, UserDTO.ReadUserDTO dto){
        User user =  userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while updating user info)"));
        if(dto.getName() != null && !dto.getName().isEmpty()){
            user.setName(dto.getName());
        }
        if(dto.getEmail() != null && !dto.getEmail().isEmpty()){
            user.setEmail(dto.getEmail());
        }

        userRepo.save(user);
    }

    public void deleteUserInfo(Long id){
        User user = userRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("⚠️ No User found! (while deleting user info)"));
        userRepo.delete(user);
    }
}
