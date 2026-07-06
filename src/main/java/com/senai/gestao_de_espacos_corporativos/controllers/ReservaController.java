package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.services.ReservaService;
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
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }



    //-- cadastrar nova reserva
    @PostMapping("/reservainserir")
    public String cadastrarReserva(@Valid @ModelAttribute("reserva")ReservaDto reservaDto, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()){ //-- bindingResult devolve o html com msg de erro sem precisar uma nova requisição
            return "reservainserir";
        }

        service.inserirReserva(reservaDto);
        redirectAttributes.addFlashAttribute("mensagem", "Reserva cadastrada com sucesso.");

        return "redirect:/reservalista";
    }


    @PostMapping("/reservaatualizar")
    public String atualizarReserva(Model model, @Valid @ModelAttribute("reserva") ReservaDto reservaDto, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "reservaatualizar";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Reserva atualizada com sucesso.");
        service.reservaAtualizar(reservaDto);

        return "redirect:/reservalista";
    }


    @DeleteMapping("/reservaexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        return ResponseEntity.ok().body("Excluido");
    }

}
