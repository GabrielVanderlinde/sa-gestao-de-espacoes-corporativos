package com.senai.gestao_de_espacos_corporativos.services;

import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.entities.ReservaEntity;
import com.senai.gestao_de_espacos_corporativos.entities.UsuarioEntity;
import com.senai.gestao_de_espacos_corporativos.repositories.ReservaRepository;
import com.senai.gestao_de_espacos_corporativos.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final ReservaRepository reservaRepository;

    public UsuarioService(UsuarioRepository repository, ReservaRepository reservaRepository) {
        this.repository = repository;
        this.reservaRepository = reservaRepository;
    }

    // LISTAR USUÁRIOS
    // Pega todos do banco e converte pra DTO.
    public List<UsuarioDto> obterListaUsuarios() {
        List<UsuarioDto> listaDto = new ArrayList<>();
        List<UsuarioEntity> listaUsuario = repository.findAll();

        for (UsuarioEntity usuarioEntity : listaUsuario) {
            listaDto.add(converterEntityParaDto(usuarioEntity));
        }
        return listaDto;
    }


    // CRIAR USUÁRIO
    // Valida email único, senha com regras, e data de nascimento.
    public void usuarioInserir(UsuarioDto usuarioDto) {
        // senha é obrigatória no cadastro
        if (usuarioDto.getSenha() == null || usuarioDto.getSenha().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória no cadastro.");
        }
        // senha precisa ter no mínimo 5 caracteres
        if (usuarioDto.getSenha().length() < 5) {
            throw new RuntimeException("Senha deve ter no mínimo 5 caracteres.");
        }
        // senha precisa ter letra E número
        if (!usuarioDto.getSenha().matches("^(?=.*[0-9])(?=.*[a-zA-Z]).*$")) {
            throw new RuntimeException("Senha deve conter letras e números.");
        }
        // email tem que ser único no sistema
        if (repository.findByEmail(usuarioDto.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
        }
        // data de nascimento não pode ter mais de 500 anos
        if (usuarioDto.getDataNascimento() != null) {
            int idade = java.time.Period.between(usuarioDto.getDataNascimento(), java.time.LocalDate.now()).getYears();
            if (idade > 500) {
                throw new RuntimeException("Data de nascimento inválida: não pode exceder 500 anos.");
            }
        }
        repository.save(converterDtoParaEntity(usuarioDto));
    }

    // BUSCAR USUÁRIO POR ID
    // Usado pra preencher o formulário de edição.
    public UsuarioDto obterUsuarioPorId(Long id) {
        UsuarioDto usuarioDto = new UsuarioDto();
        Optional<UsuarioEntity> usuarioOP = repository.findById(id);

        if (usuarioOP.isPresent()) {
            usuarioDto = converterEntityParaDto(usuarioOP.get());
        }
        return usuarioDto;
    }

    // ATUALIZAR USUÁRIO
    // Diferente de criar: senha é opcional (só valida se preencheu).
    public void usuarioAtualizar(UsuarioDto usuarioDto) {
        Optional<UsuarioEntity> usuarioOP = repository.findById(usuarioDto.getId());

        if (usuarioOP.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }

        // email tem que ser único, mas ignora o próprio usuário
        Optional<UsuarioEntity> emailExistente = repository.findByEmail(usuarioDto.getEmail());
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(usuarioDto.getId())) {
            throw new RuntimeException("E-mail já cadastrado para outro usuário.");
        }

        // só valida senha se o usuário preencheu o campo
        String senha = usuarioDto.getSenha();
        boolean senhaPreenchida = senha != null && !senha.trim().isEmpty();

        if (senhaPreenchida) {
            if (senha.trim().length() < 5) {
                throw new RuntimeException("Senha deve ter no mínimo 5 caracteres.");
            }
            if (!senha.trim().matches("^(?=.*[0-9])(?=.*[a-zA-Z]).*$")) {
                throw new RuntimeException("Senha deve conter letras e números.");
            }
        }

        UsuarioEntity usuario = usuarioOP.get();
        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setMatricula(usuarioDto.getMatricula());
        usuario.setDataNascimento(usuarioDto.getDataNascimento());
        // só atualiza a senha se foi preenchida
        if (senhaPreenchida) {
            usuario.setSenha(senha.trim());
        }
        repository.save(usuario);
    }

    // EXCLUIR USUÁRIO
    // Verifica se não tem reservas vinculadas antes de excluir.
    public void excluir(Long id) {
        Optional<UsuarioEntity> usuarioOP = repository.findById(id);
        if (usuarioOP.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }
        // não deixa excluir se tiver reserva pra esse usuário
        List<ReservaEntity> reservas = reservaRepository.findByUsuarioId(id);
        if (!reservas.isEmpty()) {
            throw new RuntimeException("Não é possível excluir o usuário pois existem reservas vinculadas. Cancele ou exclua as reservas primeiro.");
        }
        repository.deleteById(id);
    }

    // CONVERSORES
    // Entity = banco, DTO = controller/template.
    private UsuarioDto converterEntityParaDto(UsuarioEntity usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNome(usuario.getNome());
        usuarioDto.setEmail(usuario.getEmail());
        usuarioDto.setMatricula(usuario.getMatricula());
        usuarioDto.setDataNascimento(usuario.getDataNascimento());
        return usuarioDto;
    }

    private UsuarioEntity converterDtoParaEntity(UsuarioDto usuarioDto) {
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
