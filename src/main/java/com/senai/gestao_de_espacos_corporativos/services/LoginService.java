package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.entities.UsuarioEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UsuarioRepository repository;

    public LoginService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioDto realizarLogin(UsuarioDto usuarioDto ){

        Optional<UsuarioEntity> usuarioOP = repository.findByEmailAndSenha(usuarioDto.getEmail(), usuarioDto.getSenha());

        UsuarioDto usuarioDtoRetorno = new UsuarioDto();

        if (usuarioOP.isPresent()){
            //usuarioOP.get() = objeto do tipo usuarioEntity
            //usuarioDtoRetorno = usuario do tipo usuarioDto
            usuarioDtoRetorno = converterEntityParaDto(usuarioOP.get()); // usando metodo exclusivo do service para converter Dtos e Entitys sem precisar repetir quando precisar
            return usuarioDtoRetorno;
        }
        return usuarioDtoRetorno; // objeto retorna vazio
    }





    //----------------------------------------------------------------------------
    //--  novo - converter Entity para Dto - private só o service usa
    private UsuarioDto converterEntityParaDto(UsuarioEntity usuario){

        UsuarioDto usuarioDto = new UsuarioDto();

        usuarioDto.setId(usuario.getId());
        usuarioDto.setNome(usuario.getNome());
        usuarioDto.setEmail(usuario.getEmail());
        return usuarioDto;

    }

    //-- novo - converter Dto para Entity - private só o service usa
    private UsuarioEntity converterDtoParaEntity(UsuarioDto usuarioDto){

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        usuarioEntity.setId(usuarioDto.getId());
        usuarioEntity.setNome(usuarioDto.getNome());
        usuarioEntity.setEmail(usuarioDto.getEmail());
        usuarioEntity.setSenha(usuarioDto.getSenha());
        return usuarioEntity;

    }





}
