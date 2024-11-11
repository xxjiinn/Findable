package com.capstone1.findable.User.repo;

import com.capstone1.findable.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
