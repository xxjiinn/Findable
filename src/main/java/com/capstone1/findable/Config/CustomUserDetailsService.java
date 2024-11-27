package com.capstone1.findable.Config;

import com.capstone1.findable.User.entity.User;
import com.capstone1.findable.User.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // 예를 어떻게 해야 할지 고민 해봐야 함.

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("⚠️ User not found in DB: " + username);
            throw new UsernameNotFoundException("⚠️ User not found: " + username);
        }
        System.out.println("✅ User found: " + user.getUsername());
        return new CustomUserDetails(user);  // CustomUserDetails 객체 반환
    }

}
