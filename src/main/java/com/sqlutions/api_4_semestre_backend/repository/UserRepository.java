package com.sqlutions.api_4_semestre_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sqlutions.api_4_semestre_backend.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    
}

