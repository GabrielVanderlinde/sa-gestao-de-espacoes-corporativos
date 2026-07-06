package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.entities.UsuarioEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }


    public List<UsuarioDto> obterListaUsuarios(){

        List<UsuarioDto> listaDto = new ArrayList<>();

        List<UsuarioEntity> listaUsuario = repository.findAll();

        for(UsuarioEntity usuarioEntity : listaUsuario){

            listaDto.add(converterEntityParaDto(usuarioEntity));
        }
        return listaDto;
    }

    public void usuarioInserir(UsuarioDto usuarioDto){
        // Regra RF01: Senha obrigatória na criação
        if (usuarioDto.getSenha() == null || usuarioDto.getSenha().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória no cadastro.");
        }
        // Regra RF01: Unicidade via e-mail
        if (repository.findByEmail(usuarioDto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
        }
        // Regra RF01: Data de nascimento não pode ter mais de 500 anos
        if (usuarioDto.getDataNascimento() != null) {
            int idade = java.time.Period.between(usuarioDto.getDataNascimento(), java.time.LocalDate.now()).getYears();
            if (idade > 500) {
                throw new RuntimeException("Data de nascimento inválida: não pode exceder 500 anos.");
            }
        }
        repository.save(converterDtoParaEntity(usuarioDto));
    }

    public UsuarioDto obterUsuarioPorId(Long id){

        UsuarioDto usuarioDto = new UsuarioDto();
        //-- Vai na base de dados obter o usuario pelo ID
        Optional<UsuarioEntity> usuarioOP = repository.findById(id);

        if (usuarioOP.isPresent()){
            //--Converte o entity para dto
            usuarioDto = converterEntityParaDto(usuarioOP.get());
        }
        //--retorna o Dto para o controller
        return usuarioDto;
    }

    public void usuarioAtualizar(UsuarioDto usuarioDto) {

        Optional<UsuarioEntity> usuarioOP = repository.findById(usuarioDto.getId());

        if (usuarioOP.isPresent()) {
            UsuarioEntity usuario = usuarioOP.get();

            usuario.setNome(usuarioDto.getNome());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setMatricula(usuarioDto.getMatricula());
            usuario.setDataNascimento(usuarioDto.getDataNascimento());
            // Senha só atualiza se informada
            if (usuarioDto.getSenha() != null && !usuarioDto.getSenha().isEmpty()) {
                usuario.setSenha(usuarioDto.getSenha());
            }
            repository.save(usuario);
        }
    }


    public void excluir(Long id) {
        repository.deleteById(id);
    }
















    //----------------------------------------------------------------------------
    //--  novo - converter Entity para Dto - private só o service usa
    private UsuarioDto converterEntityParaDto(UsuarioEntity usuario){

        UsuarioDto usuarioDto = new UsuarioDto();

        usuarioDto.setId(usuario.getId());
        usuarioDto.setNome(usuario.getNome());
        usuarioDto.setEmail(usuario.getEmail());
        usuarioDto.setMatricula(usuario.getMatricula());
        usuarioDto.setDataNascimento(usuario.getDataNascimento());
        return usuarioDto;
    }

    //-- novo - converter Dto para Entity - private só o service usa
    private UsuarioEntity converterDtoParaEntity(UsuarioDto usuarioDto){

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        usuarioEntity.setId(usuarioDto.getId());
        usuarioEntity.setNome(usuarioDto.getNome());
        usuarioEntity.setEmail(usuarioDto.getEmail());
        usuarioEntity.setSenha(usuarioDto.getSenha());
        usuarioEntity.setMatricula(usuarioDto.getMatricula());
        usuarioEntity.setDataNascimento(usuarioDto.getDataNascimento());
        return usuarioEntity;
    }





}
