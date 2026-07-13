package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.services.RecursoService;
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
public class RecursoController {

    private final RecursoService service;

    public RecursoController(RecursoService service) {
        this.service = service;
    }


    @PostMapping("/recursoinserir")
    public String cadastrarRecurso(@Valid @ModelAttribute("recurso") RecursoDto recursoDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "recursoinserir";
        }

        try {
            service.inserirRecurso(recursoDto);
            redirectAttributes.addFlashAttribute("mensagem", "Espaço cadastrado com sucesso.");
            return "redirect:/recursolista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "recursoinserir";
        }
    }

    @PostMapping("/recursoatualizar")
    public String atualizarRecurso(Model model, @Valid @ModelAttribute("recurso") RecursoDto recursoDto, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "recursoatualizar";
        }

        try {
            service.recursoAtualizar(recursoDto);
            redirectAttributes.addFlashAttribute("mensagem", "Espaço atualizado com sucesso.");
            return "redirect:/recursolista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "recursoatualizar";
        }
    }


    @DeleteMapping("/recursoexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            return ResponseEntity.ok().body("Excluido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
