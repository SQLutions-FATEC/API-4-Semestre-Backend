package com.sqlutions.api_4_semestre_backend.service;

import org.springframework.stereotype.Service;
import com.sqlutions.api_4_semestre_backend.entity.Funcao;
import com.sqlutions.api_4_semestre_backend.entity.Usuario;
import com.sqlutions.api_4_semestre_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario salvar(Usuario usuario, Usuario salvarUsuario) {
        if (usuario.getFuncao() == Funcao.GESTOR && (salvarUsuario == null || salvarUsuario.getFuncao() != Funcao.ADMINISTRADOR)) {
            throw new RuntimeException("Somente administradores podem criar e gerenciar conta de gestores.");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    @Override
    public Usuario atualizar(Long id, Usuario atualizarUsuario) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(atualizarUsuario.getNome());
        usuario.setEmail(atualizarUsuario.getEmail());
        usuario.setSenha(atualizarUsuario.getSenha());
        usuario.setFuncao(atualizarUsuario.getFuncao());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado."));
        if (!usuario.getSenha().equals(senha)) {
            throw new RuntimeException("Senha incorreta.");
        }
        return usuario;
    }

}
