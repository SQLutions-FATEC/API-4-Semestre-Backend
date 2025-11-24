package com.sqlutions.api_4_semestre_backend.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private HashGenerator() {
        // Constructor privado para evitar instanciação
    }

    public static String generateHash(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
