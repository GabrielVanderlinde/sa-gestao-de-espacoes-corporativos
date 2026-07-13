package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.entities.RecursoEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.RecursoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecursoService {

    private final RecursoRepository repository;

    public RecursoService(RecursoRepository repository) {
        this.repository = repository;
    }


    //-- Inserir Recurso
    public void inserirRecurso(RecursoDto recursoDto) {
        if (recursoDto.getDataInicialAgendamento() != null) {
            if (recursoDto.getDataInicialAgendamento().isBefore(java.time.LocalDate.now())) {
                throw new RuntimeException("Data inicial não pode ser no passado.");
            }
        }
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
        repository.save(converterDtoParaEntity(recursoDto));
    }


    //-- Listar Recursos
    public List<RecursoDto> obterListaRecursos() {

        List<RecursoDto> listaDto = new ArrayList<>();

        List<RecursoEntity> listaRecurso = repository.findAll();

        for (RecursoEntity recursoEntity : listaRecurso) {

            listaDto.add(converterEntityParaDto(recursoEntity));
        }
        return listaDto;
    }

    public RecursoDto obterRecursoPorId(Long id) {

        RecursoDto recursoDto = new RecursoDto();
        //-- Vai na base de dados obter o usuario pelo ID
        Optional<RecursoEntity> recursoOP = repository.findById(id);

        if (recursoOP.isPresent()) {
            //--Converte o entity para dto
            recursoDto = converterEntityParaDto(recursoOP.get());
        }
        //--retorna o Dto para o controller
        return recursoDto;
    }

    public void recursoAtualizar(RecursoDto recursoDto) {

        Optional<RecursoEntity> recursoOP = repository.findById(recursoDto.getId());

        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }

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

    public void excluir(Long id) {
        Optional<RecursoEntity> recursoOP = repository.findById(id);
        if (recursoOP.isEmpty()) {
            throw new RuntimeException("Recurso não encontrado no sistema.");
        }
        repository.deleteById(id);
    }


    //--  novo - converter Entity para Dto - private só o service usa
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

    //-- novo - converter Dto para Entity - private só o service usa
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
