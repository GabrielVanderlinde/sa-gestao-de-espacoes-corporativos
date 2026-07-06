package com.senai.gestao_de_espacos_corporativos.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaDto {

    private Long id;

    //---Chaves FK
    @NotNull(message = "Usuário é obrigatório")
    private Long usuarioId;
    @NotNull(message = "Recurso é obrigatório")
    private Long recursoId;

    @NotNull(message = "Data da reserva é obrigatória")
    private LocalDate data;

    @NotNull(message = "Hora inicial é obrigatória")
    private LocalTime horaInicial;

    @NotNull(message = "Hora final é obrigatória")
    private LocalTime horaFinal;

    private LocalDate cancelamento;

    @NotBlank(message = "Observação é obrigatória no cancelamento")
    private String observacao;

    public ReservaDto() {
    }

    public ReservaDto(Long id, String observacao, LocalDate cancelamento, LocalTime horaFinal, LocalTime horaInicial, LocalDate data, Long recursoId, Long usuarioId) {
        this.id = id;
        this.observacao = observacao;
        this.cancelamento = cancelamento;
        this.horaFinal = horaFinal;
        this.horaInicial = horaInicial;
        this.data = data;
        this.recursoId = recursoId;
        this.usuarioId = usuarioId;
    }


    // getters e setters
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
