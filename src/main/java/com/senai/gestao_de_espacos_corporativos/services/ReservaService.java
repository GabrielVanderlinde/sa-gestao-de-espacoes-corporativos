package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.entities.RecursoEntity;
import com.senai.gestao_de_espacos_corporativos.entities.ReservaEntity;
import com.senai.gestao_de_espacos_corporativos.entities.UsuarioEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.RecursoRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.ReservaRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private static final int MAX_RESERVAS_POR_USUARIO = 5;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;


    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, RecursoRepository recursoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
    }


    //-- Inserir Reserva com validação de limite (Inovação 3)
    public void inserirReserva(ReservaDto reservaDto){
        if (!podeCriarReserva(reservaDto.getUsuarioId())) {
            throw new RuntimeException("Limite de reservas atingido. Máximo: " + MAX_RESERVAS_POR_USUARIO + " reservas ativas.");
        }
        reservaRepository.save(converterDtoParaEntity(reservaDto));
    }



    //-- Listar Reservas
    public List<ReservaDto> obterListaReservas(){

        List<ReservaDto> listaDto = new ArrayList<>();

        List<ReservaEntity> listaReserva = reservaRepository.findAll();

        for(ReservaEntity reservaEntity : listaReserva){

            listaDto.add(converterEntityParaDto(reservaEntity));
        }
        return listaDto;
    }


    public ReservaDto obterReservaPorId(Long id){

        ReservaDto reservaDto = new ReservaDto();
        Optional<ReservaEntity> reservaOP = reservaRepository.findById(id);

        if (reservaOP.isPresent()){
            reservaDto = converterEntityParaDto(reservaOP.get());
        }
        return reservaDto;
    }

    public void reservaAtualizar(ReservaDto reservaDto) {

        Optional<ReservaEntity> reservaOP = reservaRepository.findById(reservaDto.getId());

        if (reservaOP.isPresent()) {
            Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
            if (usuarioOP.isEmpty()) {
                throw new RuntimeException("Código de usuário não encontrado no sistema.");
            }
            Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());
            if (recursoOP.isEmpty()) {
                throw new RuntimeException("Código de recurso não encontrado no sistema.");
            }

            UsuarioEntity usuario = usuarioOP.get();
            RecursoEntity recurso = recursoOP.get();

            ReservaEntity reserva = reservaOP.get();

            reserva.setUsuario(usuario);
            reserva.setRecurso(recurso);
            reserva.setData(reservaDto.getData());
            reserva.setHoraInicial(reservaDto.getHoraInicial());
            reserva.setHoraFinal(reservaDto.getHoraFinal());
            reserva.setCancelamento(reservaDto.getCancelamento());
            reserva.setObservacao(reservaDto.getObservacao());

            reservaRepository.save(reserva);
        }
    }

    public void excluir(Long id) {reservaRepository.deleteById(id);
    }

    //=== INOVAÇÃO 1: Verificar se recurso está ocupado agora ===
    public boolean isRecursoOcupado(Long recursoId) {
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(recursoId);
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        for (ReservaEntity reserva : reservas) {
            if (reserva.getData().equals(hoje)
                    && reserva.getCancelamento() == null
                    && reserva.getHoraInicial().isBefore(agora)
                    && reserva.getHoraFinal().isAfter(agora)) {
                return true;
            }
        }
        return false;
    }

    //=== INOVAÇÃO 2: Obter horários disponíveis do recurso ===
    public RecursoDto obterHorariosRecurso(Long recursoId) {
        Optional<RecursoEntity> recursoOP = recursoRepository.findById(recursoId);
        if (recursoOP.isPresent()) {
            RecursoEntity recurso = recursoOP.get();
            RecursoDto dto = new RecursoDto();
            dto.setId(recurso.getId());
            dto.setHoraInicialAgendamento(recurso.getHoraInicialAgendamento());
            dto.setHoraFinalAgendamento(recurso.getHoraFinalAgendamento());
            return dto;
        }
        return new RecursoDto();
    }

    //=== INOVAÇÃO 3: Limite de reservas por usuário ===
    public int contarReservasAtivas(Long usuarioId) {
        List<ReservaEntity> reservas = reservaRepository.findByUsuarioId(usuarioId);
        long count = reservas.stream()
                .filter(r -> r.getCancelamento() == null)
                .count();
        return (int) count;
    }

    public boolean podeCriarReserva(Long usuarioId) {
        return contarReservasAtivas(usuarioId) < MAX_RESERVAS_POR_USUARIO;
    }

    public int getMaxReservas() {
        return MAX_RESERVAS_POR_USUARIO;
    }


    //Converter Entity para Dto
    private ReservaDto converterEntityParaDto(ReservaEntity reserva){

        ReservaDto reservaDto = new ReservaDto();

        reservaDto.setId(reserva.getId());
        reservaDto.setUsuarioId(reserva.getUsuario().getId());
        reservaDto.setRecursoId(reserva.getRecurso().getId());
        reservaDto.setData(reserva.getData());
        reservaDto.setHoraInicial(reserva.getHoraInicial());
        reservaDto.setHoraFinal(reserva.getHoraFinal());
        reservaDto.setCancelamento(reserva.getCancelamento());
        reservaDto.setObservacao(reserva.getObservacao());

        return reservaDto;
    }


    //Converter Dto para Entity
    private ReservaEntity converterDtoParaEntity(ReservaDto reservaDto){

        Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
        if (usuarioOP.isEmpty()) {
            throw new RuntimeException("Código de usuário não encontrado no sistema.");
        }
        Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());
        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Código de recurso não encontrado no sistema.");
        }

        UsuarioEntity usuario = usuarioOP.get();
        RecursoEntity recurso = recursoOP.get();

        ReservaEntity reservaEntity = new ReservaEntity();

        reservaEntity.setId(reservaDto.getId());
        reservaEntity.setUsuario(usuario);
        reservaEntity.setRecurso(recurso);
        reservaEntity.setData(reservaDto.getData());
        reservaEntity.setHoraInicial(reservaDto.getHoraInicial());
        reservaEntity.setHoraFinal(reservaDto.getHoraFinal());
        reservaEntity.setCancelamento(reservaDto.getCancelamento());
        reservaEntity.setObservacao(reservaDto.getObservacao());

        return reservaEntity;
    }


}
