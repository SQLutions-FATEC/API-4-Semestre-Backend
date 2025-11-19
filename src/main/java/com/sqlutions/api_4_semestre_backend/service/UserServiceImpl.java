package com.sqlutions.api_4_semestre_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sqlutions.api_4_semestre_backend.config.JwtUtils;
import com.sqlutions.api_4_semestre_backend.dto.LoginResponseDto;
import com.sqlutions.api_4_semestre_backend.entity.User;
import com.sqlutions.api_4_semestre_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public User saveUser(User user, User saveUser) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }

    @Override
    public List<User> listUser() {
        return userRepository.findAll();
    }

    @Override
    public User searchIdUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    @Override
    public User updateUser(Long id, User updateUser) {
        User user = searchIdUser(id);
        user.setName(updateUser.getName());
        user.setEmail(updateUser.getEmail());

        if (updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        
        user.setRole(updateUser.getRole());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado."));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Senha incorreta.");
        }
        
        String token = jwtUtils.generateJwtToken(user.getEmail(), user.getRole().name());
        
        return new LoginResponseDto(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

}
