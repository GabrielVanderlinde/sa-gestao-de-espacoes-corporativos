package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.entities.RecursoEntity;
import com.senai.gestao_de_espacos_corporativos.entities.ReservaEntity;
import com.senai.gestao_de_espacos_corporativos.entities.UsuarioEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.RecursoRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.ReservaRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    //-- Inserir Reserva
    public void inserirReserva(ReservaDto reservaDto){

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

    public void reservaAtualizar(ReservaDto reservaDto) {

        Optional<ReservaEntity> reservaOP = reservaRepository.findById(reservaDto.getId());

        if (reservaOP.isPresent()) {
            // usuarioOP.get() --> usuario com os dados do banco de dados
            // usuarioDto --> dados do usuário que vieram do formulário
            Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
            Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());

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








    //----------------------------------------------------------------------------
    //--  novo - converter Entity para Dto - private só o service usa
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


    //-- novo - converter Dto para Entity - private só o service usa
    private ReservaEntity converterDtoParaEntity(ReservaDto reservaDto){

        Optional<UsuarioEntity> usuarioOP = usuarioRepository.findById(reservaDto.getUsuarioId());
        Optional<RecursoEntity> recursoOP = recursoRepository.findById(reservaDto.getRecursoId());

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
