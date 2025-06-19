package com.capstone1.findable.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

class PasswordEncoderConfigTest {

    @Test
    void passwordEncoder_encodesAndMatches() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        BCryptPasswordEncoder encoder = config.passwordEncoder();

        String raw = "mySecret";
        String encoded = encoder.encode(raw);

        assertThat(encoded).isNotNull();
        assertThat(encoder.matches(raw, encoded)).isTrue();
        assertThat(encoder.matches("wrong", encoded)).isFalse();
    }
}