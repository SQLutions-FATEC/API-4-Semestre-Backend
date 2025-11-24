package com.sqlutions.api_4_semestre_backend.util;

import com.sqlutions.api_4_semestre_backend.config.HashGenerator;

public class PasswordHashTest {

    public static void main(String[] args) {
        String password = "admin";
        String hash = HashGenerator.generateHash(password);

        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash Generated:");
        System.out.println(hash);
    }
}

// Para gerar o hash, executar:
// mvn test-compile exec:java "-Dexec.mainClass=com.sqlutions.api_4_semestre_backend.util.PasswordHashTest" "-Dexec.classpathScope=test"