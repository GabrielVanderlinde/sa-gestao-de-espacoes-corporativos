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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ReservaService {

    // limite de reservas que cada usuário pode ter ao mesmo tempo
    private static final int MAX_RESERVAS_POR_USUARIO = 5;

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, RecursoRepository recursoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
    }

    // CRIAR RESERVA
    // Faz todas as verificações antes de salvar no banco.
    // Se qualquer uma falhar, lança erro com mensagem pro usuário.
    public void inserirReserva(ReservaDto reservaDto){
        // 1) verifica se o usuário informado existe de verdade
        Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
        if (usuarioOP.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }

        // 2) verifica se o usuário já não estourou o limite de 5 reservas
        if (!podeCriarReserva(reservaDto.getUsuarioId())) {
            throw new RuntimeException("Limite de reservas atingido. Máximo: " + MAX_RESERVAS_POR_USUARIO + " reservas ativas.");
        }

        // 3) verifica se o recurso informado existe
        Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());
        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }
        RecursoEntity recurso = recursoOP.get();

        // 4) não deixa criar reserva pra data que já passou
        if (reservaDto.getData().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível criar reserva para data passada.");
        }

        // 5) hora inicial tem que ser antes da hora final
        if (reservaDto.getHoraInicial().isAfter(reservaDto.getHoraFinal()) || reservaDto.getHoraInicial().equals(reservaDto.getHoraFinal())) {
            throw new RuntimeException("Hora inicial deve ser anterior à hora final.");
        }

        // 6) data tem que estar dentro do período que o recurso fica disponível
        if (recurso.getDataInicialAgendamento() != null && recurso.getDataFinalAgendamento() != null) {
            if (reservaDto.getData().isBefore(recurso.getDataInicialAgendamento()) ||
                reservaDto.getData().isAfter(recurso.getDataFinalAgendamento())) {
                throw new RuntimeException("Data da reserva fora do período disponível do recurso (" +
                    recurso.getDataInicialAgendamento() + " a " + recurso.getDataFinalAgendamento() + ").");
            }
        }

        // 7) dia da semana tem que estar nos dias que o recurso funciona
        if (recurso.getDiasSemanaDisponivel() != null && !recurso.getDiasSemanaDisponivel().isEmpty()) {
            String nomeDia = reservaDto.getData().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomeDia = nomeDia.substring(0, 1).toUpperCase() + nomeDia.substring(1);
            String diasDisponiveis = recurso.getDiasSemanaDisponivel().toLowerCase();
            if (!diasDisponiveis.contains(nomeDia.toLowerCase())) {
                throw new RuntimeException("O recurso não está disponível neste dia da semana (" + nomeDia + ").");
            }
        }

        // 8) horário da reserva tem que estar dentro do horário que o recurso funciona
        if (recurso.getHoraInicialAgendamento() != null && recurso.getHoraFinalAgendamento() != null) {
            if (reservaDto.getHoraInicial().isBefore(recurso.getHoraInicialAgendamento()) ||
                reservaDto.getHoraFinal().isAfter(recurso.getHoraFinalAgendamento())) {
                throw new RuntimeException("Horário fora do permitido pelo recurso (" +
                    recurso.getHoraInicialAgendamento() + " a " + recurso.getHoraFinalAgendamento() + ").");
            }
        }

        // 9) verifica se já tem reserva nesse mesmo recurso, data e horário
        if (existeConflitoAgendamento(reservaDto.getRecursoId(), reservaDto.getData(),
                reservaDto.getHoraInicial(), reservaDto.getHoraFinal(), null)) {
            throw new RuntimeException("Conflito de agendamento: este recurso já está reservado para esta data e horário.");
        }

        // se passou por tudo, salva no banco
        reservaRepository.save(converterDtoParaEntity(reservaDto));
    }

    // LISTAR RESERVAS
    // Pega todas as reservas do banco e converte pra DTO.
    public List<ReservaDto> obterListaReservas(){
        List<ReservaDto> listaDto = new ArrayList<>();
        List<ReservaEntity> listaReserva = reservaRepository.findAll();

        for(ReservaEntity reservaEntity : listaReserva){
            listaDto.add(converterEntityParaDto(reservaEntity));
        }
        return listaDto;
    }

    // BUSCAR RESERVA POR ID
    // Usado pra preencher a tela de visualizar e cancelar.
    public ReservaDto obterReservaPorId(Long id){
        ReservaDto reservaDto = new ReservaDto();
        Optional<ReservaEntity> reservaOP = reservaRepository.findById(id);

        if (reservaOP.isPresent()){
            reservaDto = converterEntityParaDto(reservaOP.get());
        }
        return reservaDto;
    }

    // EXCLUIR RESERVA
    // Remove do banco permanentemente.
    public void excluir(Long id) {
        reservaRepository.deleteById(id);
    }

    // CANCELAR RESERVA
    // Não apaga do banco, só marca a data de cancelamento.
    // Exige motivo e pelo menos 1 dia de antecedência.
    public void cancelarReserva(ReservaDto reservaDto) {
        // motivo é obrigatório
        if (reservaDto.getObservacao() == null || reservaDto.getObservacao().trim().isEmpty()) {
            throw new RuntimeException("Observação é obrigatória para cancelar a reserva.");
        }

        Optional<ReservaEntity> reservaOP = reservaRepository.findById(reservaDto.getId());
        if (reservaOP.isEmpty()) {
            throw new RuntimeException("Reserva não encontrada no sistema.");
        }

        ReservaEntity reserva = reservaOP.get();

        // não deixa cancelar se já foi cancelada antes
        if (reserva.getCancelamento() != null) {
            throw new RuntimeException("Esta reserva já foi cancelada.");
        }

        // precisa ter pelo menos 1 dia de antecedência
        long diasAteReserva = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), reserva.getData());
        if (diasAteReserva < 1) {
            throw new RuntimeException("O cancelamento só pode ser realizado com pelo menos 1 dia de antecedência.");
        }

        // grava a data de hoje como data de cancelamento
        reserva.setCancelamento(LocalDate.now());
        reserva.setObservacao(reservaDto.getObservacao().trim());
        reservaRepository.save(reserva);
    }

    // INOVAÇÃO 1 - STATUS DO RECURSO (verde/vermelho)
    // Verifica se o recurso está sendo usado agora.
    public boolean isRecursoOcupado(Long recursoId) {
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(recursoId);
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        for (ReservaEntity reserva : reservas) {
            // se tem reserva pra hoje, não cancelada, e o horário atual tá dentro
            if (reserva.getData().equals(hoje)
                    && reserva.getCancelamento() == null
                    && reserva.getHoraInicial().isBefore(agora)
                    && reserva.getHoraFinal().isAfter(agora)) {
                return true; // tá ocupado
            }
        }
        return false; // tá livre
    }

    // INOVAÇÃO 2 - HORÁRIO AUTOMÁTICO
    // Retorna os horários que o recurso fica disponível.
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


    // INOVAÇÃO 3 - LIMITE DE RESERVAS POR USUÁRIO
    // Conta só as reservas ativas (não canceladas).
    public int contarReservasAtivas(Long usuarioId) {
        List<ReservaEntity> reservas = reservaRepository.findByUsuarioId(usuarioId);
        long count = reservas.stream()
                .filter(r -> r.getCancelamento() == null) // só as que não foram canceladas
                .count();
        return (int) count;
    }

    public boolean podeCriarReserva(Long usuarioId) {
        return contarReservasAtivas(usuarioId) < MAX_RESERVAS_POR_USUARIO;
    }

    public int getMaxReservas() {
        return MAX_RESERVAS_POR_USUARIO;
    }


    // VERIFICAR CONFLITO DE AGENDAMENTO
    // Checa se já tem reserva no mesmo recurso, data e horário.
    // Ignora reservas canceladas e a própria reserva (quando edita).
    private boolean existeConflitoAgendamento(Long recursoId, LocalDate data, LocalTime horaInicial, LocalTime horaFinal, Long reservaIdExcluir) {
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(recursoId);
        for (ReservaEntity reserva : reservas) {
            // pula se for a própria reserva (caso de edição)
            if (reserva.getId().equals(reservaIdExcluir)) {
                continue;
            }
            // pula se já foi cancelada
            if (reserva.getCancelamento() != null) {
                continue;
            }
            // se for o mesmo dia, verifica se os horários se sobrepõem
            if (reserva.getData().equals(data)) {
                boolean sobrepor = !horaFinal.isBefore(reserva.getHoraInicial()) && !horaInicial.isAfter(reserva.getHoraFinal());
                if (sobrepor) {
                    return true; // tem conflito
                }
            }
        }
        return false; // sem conflito
    }

    // CONVERSORES
    // Trocam Entity pra DTO e vice-versa.
    // Entity = como está no banco, DTO = o que vai pro template/controller.
    private ReservaDto converterEntityParaDto(ReservaEntity reserva){
        ReservaDto reservaDto = new ReservaDto();

        reservaDto.setId(reserva.getId());
        reservaDto.setUsuarioId(reserva.getUsuario().getId());
        reservaDto.setRecursoId(reserva.getRecurso().getId());
        reservaDto.setNomeUsuario(reserva.getUsuario().getNome());
        reservaDto.setDescricaoRecurso(reserva.getRecurso().getDescricao());
        reservaDto.setData(reserva.getData());
        reservaDto.setHoraInicial(reserva.getHoraInicial());
        reservaDto.setHoraFinal(reserva.getHoraFinal());
        reservaDto.setCancelamento(reserva.getCancelamento());
        reservaDto.setObservacao(reserva.getObservacao());

        return reservaDto;
    }

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
