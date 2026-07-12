package com.senai.gestao_de_espacos_corporativos.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

public class RecursoDto {

    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "Tipo de recurso é obrigatório")
    private String tipo;

    private String diasSemanaDisponivel;

    @FutureOrPresent(message = "Data inicial deve ser hoje ou futura")
    private LocalDate dataInicialAgendamento;

    @FutureOrPresent(message = "Data final deve ser hoje ou futura")
    private LocalDate dataFinalAgendamento;

    private LocalTime horaInicialAgendamento;
    private LocalTime horaFinalAgendamento;

    public RecursoDto() {
    }

    public RecursoDto(Long id, String descricao, String tipo, String diasSemanaDisponivel, LocalDate dataInicialAgendamento, LocalDate dataFinalAgendamento, LocalTime horaInicialAgendamento, LocalTime horaFinalAgendamento) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.diasSemanaDisponivel = diasSemanaDisponivel;
        this.dataInicialAgendamento = dataInicialAgendamento;
        this.dataFinalAgendamento = dataFinalAgendamento;
        this.horaInicialAgendamento = horaInicialAgendamento;
        this.horaFinalAgendamento = horaFinalAgendamento;
    }


    // getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDiasSemanaDisponivel() {
        return diasSemanaDisponivel;
    }

    public void setDiasSemanaDisponivel(String diasSemanaDisponivel) {
        this.diasSemanaDisponivel = diasSemanaDisponivel;
    }

    public LocalDate getDataInicialAgendamento() {
        return dataInicialAgendamento;
    }

    public void setDataInicialAgendamento(LocalDate dataInicialAgendamento) {
        this.dataInicialAgendamento = dataInicialAgendamento;
    }

    public LocalDate getDataFinalAgendamento() {
        return dataFinalAgendamento;
    }

    public void setDataFinalAgendamento(LocalDate dataFinalAgendamento) {
        this.dataFinalAgendamento = dataFinalAgendamento;
    }

    public LocalTime getHoraInicialAgendamento() {
        return horaInicialAgendamento;
    }

    public void setHoraInicialAgendamento(LocalTime horaInicialAgendamento) {
        this.horaInicialAgendamento = horaInicialAgendamento;
    }

    public LocalTime getHoraFinalAgendamento() {
        return horaFinalAgendamento;
    }

    public void setHoraFinalAgendamento(LocalTime horaFinalAgendamento) {
        this.horaFinalAgendamento = horaFinalAgendamento;
    }

}
