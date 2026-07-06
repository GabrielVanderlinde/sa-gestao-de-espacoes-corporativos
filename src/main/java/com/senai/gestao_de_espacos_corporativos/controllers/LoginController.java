package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.services.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final LoginService service;

    public LoginController(LoginService service) {
        this.service = service;
    }


    @PostMapping("/login")
    public String realizarLogin(String email, String senha, Model model, RedirectAttributes redirectAttributes){ // redirectAttributes retorna um comando, se for só model retorna html

        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setEmail(email);
        usuarioDto.setSenha(senha);

        UsuarioDto usuarioDtoRetorno = service.realizarLogin(usuarioDto);

        if (usuarioDtoRetorno.getNome() != null) {
            redirectAttributes.addFlashAttribute("usuario", "Bem-vindo Usuário! -   " + usuarioDtoRetorno.getNome()); // flash mantem volor persistente
            return "redirect:/home";
        }
        model.addAttribute("erro", "E-mail ou senha inválidos");
        return "login";  // -- retorna um HTML
    }


}
