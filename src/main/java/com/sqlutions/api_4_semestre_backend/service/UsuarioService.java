package com.sqlutions.api_4_semestre_backend.service;

import com.sqlutions.api_4_semestre_backend.entity.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario salvar(Usuario usuario, Usuario salvarUsuario);

    List<Usuario> listar();

    Usuario buscarPorId(Long id);

    Usuario atualizar(Long id, Usuario atualizarUsuario);

    void deletar(Long id);

    Usuario login(String email, String senha);
}

