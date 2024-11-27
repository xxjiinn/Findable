package com.capstone1.findable.oauth.repo;

import com.capstone1.findable.oauth.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedTokenRepo extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token); // 블랙리스트에 있는 토큰 확인
}
