package com.capstone1.findable.User.repo;

import com.capstone1.findable.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    public Optional<User> findByEmail(String email);

    public User findByUsername(String username);
}
