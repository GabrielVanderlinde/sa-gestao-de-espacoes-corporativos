package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }


    //-- cadastrar nova reserva com validação de limite
    @PostMapping("/reservainserir")
    public String cadastrarReserva(@Valid @ModelAttribute("reserva") ReservaDto reservaDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "reservainserir";
        }

        try {
            service.inserirReserva(reservaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Reserva cadastrada com sucesso.");
            return "redirect:/reservalista";
        } catch (RuntimeException e) {
            String chave = e.getMessage().contains("Limite") ? "erroLimite" : "erro";
            model.addAttribute(chave, e.getMessage());
            return "reservainserir";
        }
    }


    //-- cancelar reserva
    @PostMapping("/reservacancelar")
    public String cancelarReserva(@Valid @ModelAttribute("reserva") ReservaDto reservaDto, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "reservaatualizar";
        }
        try {
            service.cancelarReserva(reservaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Reserva cancelada com sucesso.");
            return "redirect:/reservalista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "reservaatualizar";
        }
    }


    @DeleteMapping("/reservaexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        return ResponseEntity.ok().body("Excluido");
    }

    //Endpoint REST para horários do recurso (horário automático)
    @GetMapping("/api/recurso/{id}/horarios")
    @ResponseBody
    public RecursoDto obterHorariosRecurso(@PathVariable Long id) {
        return service.obterHorariosRecurso(id);
    }

    //Endpoint REST para status do recurso (indicador visual)
    @GetMapping("/api/recurso/{id}/status")
    @ResponseBody
    public java.util.Map<String, Object> obterStatusRecurso(@PathVariable Long id) {
        boolean ocupado = service.isRecursoOcupado(id);
        return java.util.Map.of("ocupado", ocupado, "status", ocupado ? "Ocupado" : "Livre");
    }

    //Endpoint REST para contar reservas do usuário
    @GetMapping("/api/usuario/{id}/reservas")
    @ResponseBody
    public java.util.Map<String, Object> contarReservasUsuario(@PathVariable Long id) {
        int count = service.contarReservasAtivas(id);
        int max = service.getMaxReservas();
        return java.util.Map.of("count", count, "max", max, "podeCriar", count < max);
    }

}
