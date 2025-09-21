package com.sqlutions.api_4_semestre_backend.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long id;

    @Column(nullable = false, name = "usr_nome")
    private String nome;

    @Column(nullable = false, unique = true, name = "usr_email")
    private String email;

    @Column(nullable = false, name = "usr_senha")
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role funcao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getFuncao() {
        return funcao;
    }

    public void setFuncao(Role funcao) {
        this.funcao = funcao;
    }

}
