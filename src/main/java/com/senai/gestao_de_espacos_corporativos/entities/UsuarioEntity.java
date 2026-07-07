package com.senai.gestao_de_espacos_corporativos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "E-mail deve ser válido")
    @NotBlank(message = "E-mail é obrigatório")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 5, message = "Senha deve ter no mínimo 5 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).*$",
            message = "Senha deve conter letras e números")
    private String senha;

    @NotBlank(message = "Matrícula é obrigatória")
    private String matricula;

    @Past(message = "Data de nascimento deve ser no passado")
    private java.time.LocalDate dataNascimento;


    public UsuarioEntity() {
    }

    public UsuarioEntity(Long id, String nome, String email, String senha, String matricula, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
    }


    // getters e setters
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


}
