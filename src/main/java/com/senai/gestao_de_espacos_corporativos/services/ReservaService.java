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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
        if (usuarioOP.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }

        if (!podeCriarReserva(reservaDto.getUsuarioId())) {
            throw new RuntimeException("Limite de reservas atingido. Máximo: " + MAX_RESERVAS_POR_USUARIO + " reservas ativas.");
        }

        Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());
        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }
        RecursoEntity recurso = recursoOP.get();

        if (reservaDto.getData().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível criar reserva para data passada.");
        }

        if (reservaDto.getHoraInicial().isAfter(reservaDto.getHoraFinal()) || reservaDto.getHoraInicial().equals(reservaDto.getHoraFinal())) {
            throw new RuntimeException("Hora inicial deve ser anterior à hora final.");
        }

        if (recurso.getDataInicialAgendamento() != null && recurso.getDataFinalAgendamento() != null) {
            if (reservaDto.getData().isBefore(recurso.getDataInicialAgendamento()) ||
                reservaDto.getData().isAfter(recurso.getDataFinalAgendamento())) {
                throw new RuntimeException("Data da reserva fora do período disponível do recurso (" +
                    recurso.getDataInicialAgendamento() + " a " + recurso.getDataFinalAgendamento() + ").");
            }
        }

        if (recurso.getDiasSemanaDisponivel() != null && !recurso.getDiasSemanaDisponivel().isEmpty()) {
            String nomeDia = reservaDto.getData().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomeDia = nomeDia.substring(0, 1).toUpperCase() + nomeDia.substring(1);
            String diasDisponiveis = recurso.getDiasSemanaDisponivel().toLowerCase();
            if (!diasDisponiveis.contains(nomeDia.toLowerCase())) {
                throw new RuntimeException("O recurso não está disponível neste dia da semana (" + nomeDia + ").");
            }
        }

        if (recurso.getHoraInicialAgendamento() != null && recurso.getHoraFinalAgendamento() != null) {
            if (reservaDto.getHoraInicial().isBefore(recurso.getHoraInicialAgendamento()) ||
                reservaDto.getHoraFinal().isAfter(recurso.getHoraFinalAgendamento())) {
                throw new RuntimeException("Horário fora do permitido pelo recurso (" +
                    recurso.getHoraInicialAgendamento() + " a " + recurso.getHoraFinalAgendamento() + ").");
            }
        }

        if (existeConflitoAgendamento(reservaDto.getRecursoId(), reservaDto.getData(),
                reservaDto.getHoraInicial(), reservaDto.getHoraFinal(), null)) {
            throw new RuntimeException("Conflito de agendamento: este recurso já está reservado para esta data e horário.");
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

            if (reserva.getCancelamento() != null) {
                throw new RuntimeException("Não é possível atualizar uma reserva cancelada.");
            }

            if (reservaDto.getData().isBefore(LocalDate.now())) {
                throw new RuntimeException("Não é possível atualizar reserva para data passada.");
            }

            if (reservaDto.getHoraInicial().isAfter(reservaDto.getHoraFinal()) || reservaDto.getHoraInicial().equals(reservaDto.getHoraFinal())) {
                throw new RuntimeException("Hora inicial deve ser anterior à hora final.");
            }

            if (recurso.getDataInicialAgendamento() != null && recurso.getDataFinalAgendamento() != null) {
                if (reservaDto.getData().isBefore(recurso.getDataInicialAgendamento()) ||
                    reservaDto.getData().isAfter(recurso.getDataFinalAgendamento())) {
                    throw new RuntimeException("Data da reserva fora do período disponível do recurso.");
                }
            }

            if (recurso.getHoraInicialAgendamento() != null && recurso.getHoraFinalAgendamento() != null) {
                if (reservaDto.getHoraInicial().isBefore(recurso.getHoraInicialAgendamento()) ||
                    reservaDto.getHoraFinal().isAfter(recurso.getHoraFinalAgendamento())) {
                    throw new RuntimeException("Horário fora do permitido pelo recurso.");
                }
            }

            if (existeConflitoAgendamento(reservaDto.getRecursoId(), reservaDto.getData(),
                    reservaDto.getHoraInicial(), reservaDto.getHoraFinal(), reservaDto.getId())) {
                throw new RuntimeException("Conflito de agendamento: este recurso já está reservado para esta data e horário.");
            }

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

    public void cancelarReserva(ReservaDto reservaDto) {
        Optional<ReservaEntity> reservaOP = reservaRepository.findById(reservaDto.getId());
        if (reservaOP.isPresent()) {
            ReservaEntity reserva = reservaOP.get();

            if (reserva.getCancelamento() != null) {
                throw new RuntimeException("Esta reserva já foi cancelada.");
            }

            long diasAteReserva = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), reserva.getData());
            if (diasAteReserva < 1) {
                throw new RuntimeException("O cancelamento só pode ser realizado com pelo menos 1 dia de antecedência.");
            }
            reserva.setCancelamento(LocalDate.now());
            reserva.setObservacao(reservaDto.getObservacao());
            reservaRepository.save(reserva);
        }
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

    private boolean existeConflitoAgendamento(Long recursoId, LocalDate data, LocalTime horaInicial, LocalTime horaFinal, Long reservaIdExcluir) {
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(recursoId);
        for (ReservaEntity reserva : reservas) {
            if (reserva.getId().equals(reservaIdExcluir)) {
                continue;
            }
            if (reserva.getCancelamento() != null) {
                continue;
            }
            if (reserva.getData().equals(data)) {
                boolean sobrepor = !horaFinal.isBefore(reserva.getHoraInicial()) && !horaInicial.isAfter(reserva.getHoraFinal());
                if (sobrepor) {
                    return true;
                }
            }
        }
        return false;
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
