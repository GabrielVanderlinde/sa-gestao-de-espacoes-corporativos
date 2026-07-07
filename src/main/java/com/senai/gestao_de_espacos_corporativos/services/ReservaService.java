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

import java.time.temporal.ChronoUnit;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, RecursoRepository recursoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
    }


    //-- Inserir Reserva com validação de limite (Inovação 3) e conflito de agendamento (RF06)
    public void inserirReserva(ReservaDto reservaDto){
        if (!podeCriarReserva(reservaDto.getUsuarioId())) {
            throw new RuntimeException("Limite de reservas atingido. Máximo: " + MAX_RESERVAS_POR_USUARIO + " reservas ativas.");
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
        //-- Vai na base de dados obter o usuario pelo ID
        Optional<ReservaEntity> reservaOP = reservaRepository.findById(id);

        if (reservaOP.isPresent()){
            //--Converte o entity para dto
            reservaDto = converterEntityParaDto(reservaOP.get());
        }
        //--retorna o Dto para o controller
        return reservaDto;
    }

    public void excluir(Long id) {reservaRepository.deleteById(id);
    }

    //-- Cancelar reserva
    public void cancelarReserva(ReservaDto reservaDto) {
        Optional<ReservaEntity> reservaOP = reservaRepository.findById(reservaDto.getId());
        if (reservaOP.isPresent()) {
            ReservaEntity reserva = reservaOP.get();
            // cancelamento só pode ocorrer 1 dia antes da data agendada
            long diasAteReserva = ChronoUnit.DAYS.between(LocalDate.now(), reserva.getData());
            if (diasAteReserva < 1) {
                throw new RuntimeException("O cancelamento só pode ser realizado com pelo menos 1 dia de antecedência da data agendada.");
            }
            reserva.setCancelamento(LocalDate.now());
            reserva.setObservacao(reservaDto.getObservacao());
            reservaRepository.save(reserva);
        }
    }

    //Verificar se há conflito de agendamento para o mesmo recurso, data e horário ===
    private boolean existeConflitoAgendamento(Long recursoId, LocalDate data, LocalTime horaInicial, LocalTime horaFinal, Long reservaIdExcluir) {
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(recursoId);
        for (ReservaEntity reserva : reservas) {
            // Ignorar a própria reserva (para cancelamento sem conflito)
            if (reservaIdExcluir != null && reserva.getId().equals(reservaIdExcluir)) {
                continue;
            }
            // Ignorar reservas canceladas
            if (reserva.getCancelamento() != null) {
                continue;
            }
            // Mesma data e horário sobreposto
            if (reserva.getData().equals(data)) {
                boolean sobrepor = horaInicial.isBefore(reserva.getHoraFinal()) && horaFinal.isAfter(reserva.getHoraInicial());
                if (sobrepor) {
                    return true;
                }
            }
        }
        return false;
    }

    //Verificar se recurso está ocupado agora
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

    //Obter horários disponíveis do recurso
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

    //Limite de reservas por usuário ===
    private static final int MAX_RESERVAS_POR_USUARIO = 5;

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








    //Converter Entity para Dto - private só o service usa
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


    //Converter Dto para Entity - private só o service usa
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
