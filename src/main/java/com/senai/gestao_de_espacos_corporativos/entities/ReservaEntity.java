package com.senai.gestao_de_espacos_corporativos.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //---Chaves FK
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
    @ManyToOne(optional = false)
    @JoinColumn(name = "recurso_id")
    private RecursoEntity recurso;


    @NotNull(message = "Data da reserva é obrigatória")
    private java.time.LocalDate data;

    @NotNull(message = "Hora inicial é obrigatória")
    private java.time.LocalTime horaInicial;

    @NotNull(message = "Hora final é obrigatória")
    private java.time.LocalTime horaFinal;

    private java.time.LocalDate cancelamento;

    @Column(length = 255)
    private String observacao;



    public ReservaEntity() {
    }

    public ReservaEntity(Long id, UsuarioEntity usuario, RecursoEntity recurso, LocalDate data, LocalTime horaInicial, LocalTime horaFinal, LocalDate cancelamento, String observacao) {
        this.id = id;
        this.usuario = usuario;
        this.recurso = recurso;
        this.data = data;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
        this.cancelamento = cancelamento;
        this.observacao = observacao;
    }


    // getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public RecursoEntity getRecurso() {
        return recurso;
    }

    public void setRecurso(RecursoEntity recurso) {
        this.recurso = recurso;
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
