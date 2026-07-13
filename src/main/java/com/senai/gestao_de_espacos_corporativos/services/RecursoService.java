package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.entities.RecursoEntity;
import com.senai.gestao_de_espacos_corporativos.entities.ReservaEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.RecursoRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecursoService {

    private final RecursoRepository repository;
    private final ReservaRepository reservaRepository;

    public RecursoService(RecursoRepository repository, ReservaRepository reservaRepository) {
        this.repository = repository;
        this.reservaRepository = reservaRepository;
    }

    // CRIAR RECURSO
    // Valida datas e horários antes de salvar.
    // Na criação, data inicial não pode ser no passado.
    public void inserirRecurso(RecursoDto recursoDto) {
        // data inicial não pode ser no passado (só na criação)
        if (recursoDto.getDataInicialAgendamento() != null) {
            if (recursoDto.getDataInicialAgendamento().isBefore(java.time.LocalDate.now())) {
                throw new RuntimeException("Data inicial não pode ser no passado.");
            }
        }
        // data inicial tem que ser antes da final
        if (recursoDto.getDataInicialAgendamento() != null && recursoDto.getDataFinalAgendamento() != null) {
            if (recursoDto.getDataInicialAgendamento().isAfter(recursoDto.getDataFinalAgendamento())) {
                throw new RuntimeException("Data inicial não pode ser posterior à data final.");
            }
        }
        // hora inicial tem que ser antes da final
        if (recursoDto.getHoraInicialAgendamento() != null && recursoDto.getHoraFinalAgendamento() != null) {
            if (!recursoDto.getHoraInicialAgendamento().isBefore(recursoDto.getHoraFinalAgendamento())) {
                throw new RuntimeException("Hora inicial deve ser anterior à hora final.");
            }
        }
        repository.save(converterDtoParaEntity(recursoDto));
    }

    // LISTAR RECURSOS
    // Pega todos do banco e converte pra DTO.
    public List<RecursoDto> obterListaRecursos() {
        List<RecursoDto> listaDto = new ArrayList<>();
        List<RecursoEntity> listaRecurso = repository.findAll();

        for (RecursoEntity recursoEntity : listaRecurso) {
            listaDto.add(converterEntityParaDto(recursoEntity));
        }
        return listaDto;
    }


    // BUSCAR RECURSO POR ID
    // Usado pra preencher o formulário de edição.
    public RecursoDto obterRecursoPorId(Long id) {
        RecursoDto recursoDto = new RecursoDto();
        Optional<RecursoEntity> recursoOP = repository.findById(id);

        if (recursoOP.isPresent()) {
            recursoDto = converterEntityParaDto(recursoOP.get());
        }
        return recursoDto;
    }


    // ATUALIZAR RECURSO
    // Na atualização NÃO valida data no passado (recurso pode ter sido criado antes).
    public void recursoAtualizar(RecursoDto recursoDto) {
        Optional<RecursoEntity> recursoOP = repository.findById(recursoDto.getId());

        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }

        // só valida relação entre datas
        if (recursoDto.getDataInicialAgendamento() != null && recursoDto.getDataFinalAgendamento() != null) {
            if (recursoDto.getDataInicialAgendamento().isAfter(recursoDto.getDataFinalAgendamento())) {
                throw new RuntimeException("Data inicial não pode ser posterior à data final.");
            }
        }
        if (recursoDto.getHoraInicialAgendamento() != null && recursoDto.getHoraFinalAgendamento() != null) {
            if (!recursoDto.getHoraInicialAgendamento().isBefore(recursoDto.getHoraFinalAgendamento())) {
                throw new RuntimeException("Hora inicial deve ser anterior à hora final.");
            }
        }

        RecursoEntity recurso = recursoOP.get();
        recurso.setDescricao(recursoDto.getDescricao());
        recurso.setTipo(recursoDto.getTipo());
        recurso.setDiasSemanaDisponivel(recursoDto.getDiasSemanaDisponivel());
        recurso.setDataInicialAgendamento(recursoDto.getDataInicialAgendamento());
        recurso.setDataFinalAgendamento(recursoDto.getDataFinalAgendamento());
        recurso.setHoraInicialAgendamento(recursoDto.getHoraInicialAgendamento());
        recurso.setHoraFinalAgendamento(recursoDto.getHoraFinalAgendamento());

        repository.save(recurso);
    }

    // EXCLUIR RECURSO
    // Verifica se não tem reservas vinculadas antes de excluir.
    public void excluir(Long id) {
        Optional<RecursoEntity> recursoOP = repository.findById(id);
        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }
        // não deixa excluir se tiver reserva pra esse recurso
        List<ReservaEntity> reservas = reservaRepository.findByRecursoId(id);
        if (!reservas.isEmpty()) {
            throw new RuntimeException("Não é possível excluir o recurso pois existem reservas vinculadas. Cancele ou exclua as reservas primeiro.");
        }
        repository.deleteById(id);
    }


    // CONVERSORES
    // Entity = banco, DTO = controller/template.
    private RecursoDto converterEntityParaDto(RecursoEntity recurso) {
        RecursoDto recursoDto = new RecursoDto();
        recursoDto.setId(recurso.getId());
        recursoDto.setDescricao(recurso.getDescricao());
        recursoDto.setTipo(recurso.getTipo());
        recursoDto.setDiasSemanaDisponivel(recurso.getDiasSemanaDisponivel());
        recursoDto.setDataInicialAgendamento(recurso.getDataInicialAgendamento());
        recursoDto.setDataFinalAgendamento(recurso.getDataFinalAgendamento());
        recursoDto.setHoraInicialAgendamento(recurso.getHoraInicialAgendamento());
        recursoDto.setHoraFinalAgendamento(recurso.getHoraFinalAgendamento());
        return recursoDto;
    }

    private RecursoEntity converterDtoParaEntity(RecursoDto recursoDto) {
        RecursoEntity recursoEntity = new RecursoEntity();
        recursoEntity.setId(recursoDto.getId());
        recursoEntity.setDescricao(recursoDto.getDescricao());
        recursoEntity.setTipo(recursoDto.getTipo());
        recursoEntity.setDiasSemanaDisponivel(recursoDto.getDiasSemanaDisponivel());
        recursoEntity.setDataInicialAgendamento(recursoDto.getDataInicialAgendamento());
        recursoEntity.setDataFinalAgendamento(recursoDto.getDataFinalAgendamento());
        recursoEntity.setHoraInicialAgendamento(recursoDto.getHoraInicialAgendamento());
        recursoEntity.setHoraFinalAgendamento(recursoDto.getHoraFinalAgendamento());
        return recursoEntity;
    }

}
