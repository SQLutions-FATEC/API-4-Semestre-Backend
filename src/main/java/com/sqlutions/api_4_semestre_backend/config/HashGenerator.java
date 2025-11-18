package com.sqlutions.api_4_semestre_backend.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        String senhaPura = "admin";
        String hash = passwordEncoder.encode(senhaPura);

        System.out.println("Hash BCrypt Gerado:");
        System.out.println(hash);
    } 
}
