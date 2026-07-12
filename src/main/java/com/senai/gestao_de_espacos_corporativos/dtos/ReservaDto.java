package com.senai.gestao_de_espacos_corporativos.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaDto {

    private Long id;

    @NotNull(message = "Usuário é obrigatório")
    private Long usuarioId;
    @NotNull(message = "Recurso é obrigatório")
    private Long recursoId;

    private String nomeUsuario;
    private String descricaoRecurso;

    @NotNull(message = "Data da reserva é obrigatória")
    private LocalDate data;

    @NotNull(message = "Hora inicial é obrigatória")
    private LocalTime horaInicial;

    @NotNull(message = "Hora final é obrigatória")
    private LocalTime horaFinal;

    private LocalDate cancelamento;

    private String observacao;

    public ReservaDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(LocalTime horaInicial) {
        this.horaInicial = horaInicial;
    }

    public LocalTime getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(LocalTime horaFinal) {
        this.horaFinal = horaFinal;
    }

    public LocalDate getCancelamento() {
        return cancelamento;
    }

    public void setCancelamento(LocalDate cancelamento) {
        this.cancelamento = cancelamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

}
