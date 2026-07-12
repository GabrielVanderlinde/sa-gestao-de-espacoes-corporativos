package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }


    @PostMapping("/usuarioinserir")
    public String inserirUsuario(@Valid @ModelAttribute("usuario") UsuarioDto usuarioDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "usuarioinserir";
        }
        try {
            service.usuarioInserir(usuarioDto);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso.");
            return "redirect:/usuariolista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "usuarioinserir";
        }
    }

    @PostMapping("/usuarioatualizar")
    public String atualizarUsuario(Model model, @Valid @ModelAttribute("usuario") UsuarioDto usuarioDto, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "usuarioatualizar";
        }

        try {
            service.usuarioAtualizar(usuarioDto);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso.");
            return "redirect:/usuariolista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "usuarioatualizar";
        }
    }


    @DeleteMapping("/usuarioexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        return ResponseEntity.ok().body("Excluido");
    }


}
