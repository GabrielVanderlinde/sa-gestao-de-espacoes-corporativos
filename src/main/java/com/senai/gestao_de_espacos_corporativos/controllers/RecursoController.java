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


    //-- cadastrar novo recurso
    @PostMapping("/recursoinserir")
    public String cadastrarRecurso(@Valid @ModelAttribute("recurso") RecursoDto recursoDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) { //-- bindingResult devolve o html com msg de erro sem precisar uma nova requisição
            return "recursoinserir";
        }

        service.inserirRecurso(recursoDto);
        redirectAttributes.addFlashAttribute("mensagem", "Espaço cadastrado com sucesso.");

        return "redirect:/recursolista";
    }

    @PostMapping("/recursoatualizar")
    public String atualizarRecurso(Model model, @Valid @ModelAttribute("recurso") RecursoDto recursoDto, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "recursoatualizar";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Espaço atualizado com sucesso.");
        service.recursoAtualizar(recursoDto);

        return "redirect:/recursolista";
    }


    @DeleteMapping("/recursoexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        return ResponseEntity.ok().body("Excluido");
    }


}
