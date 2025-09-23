package com.sqlutions.api_4_semestre_backend.service;

import org.springframework.stereotype.Service;
import com.sqlutions.api_4_semestre_backend.entity.Role;
import com.sqlutions.api_4_semestre_backend.entity.User;
import com.sqlutions.api_4_semestre_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user, User saveUser) {
        if (user.getRole() == Role.GESTOR && (saveUser == null || saveUser.getRole() != Role.ADMINISTRADOR)) {
            throw new RuntimeException("Somente administradores podem criar e gerenciar conta de gestores.");
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
        user.setPassword(updateUser.getPassword());
        user.setRole(updateUser.getRole());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado."));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Senha incorreta.");
        }
        return user;
    }

}
