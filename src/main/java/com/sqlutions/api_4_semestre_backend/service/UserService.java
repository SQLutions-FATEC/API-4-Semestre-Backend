package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.User;
import java.util.List;

public interface UserService {

    User saveUser(User user, User salvarUser);

    List<User> listUser();

    User searchIdUser(Long id);

    User updateUser(Long id, User atualizarUser);

    void deleteUser(Long id);

    User login(String email, String password);
}

