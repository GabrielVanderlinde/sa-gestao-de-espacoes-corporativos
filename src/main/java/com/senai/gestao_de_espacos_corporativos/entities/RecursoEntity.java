package com.senai.gestao_de_espacos_corporativos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "recursos")
public class RecursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "Tipo de recurso é obrigatório")
    private String tipo;

    @Column(length = 500)
    private String diasSemanaDisponivel;

    private java.time.LocalDate dataInicialAgendamento;

    private java.time.LocalDate dataFinalAgendamento;

    private java.time.LocalTime horaInicialAgendamento;
    private java.time.LocalTime horaFinalAgendamento;


    public RecursoEntity() {
    }

    public RecursoEntity(Long id, String descricao, String tipo, String diasSemanaDisponivel, LocalDate dataInicialAgendamento, LocalDate dataFinalAgendamento, LocalTime horaInicialAgendamento, LocalTime horaFinalAgendamento) {
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
