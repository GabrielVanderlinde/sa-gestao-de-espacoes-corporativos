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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }



    //-- cadastrar nova reserva
    @PostMapping("/reservainserir")
    public String cadastrarReserva(@Valid @ModelAttribute("reserva")ReservaDto reservaDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){

        if (bindingResult.hasErrors()){
            return "reservainserir";
        }

        try {
            service.inserirReserva(reservaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Reserva cadastrada com sucesso.");
            return "redirect:/reservalista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "reservainserir";
        }
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

    //=== INOVAÇÃO: Endpoint REST para horários do recurso (horário automático) ===
    @GetMapping("/api/recurso/{id}/horarios")
    @ResponseBody
    public RecursoDto obterHorariosRecurso(@PathVariable Long id) {
        return service.obterHorariosRecurso(id);
    }

    //=== INOVAÇÃO: Endpoint REST para status do recurso (indicador visual) ===
    @GetMapping("/api/recurso/{id}/status")
    @ResponseBody
    public java.util.Map<String, Object> obterStatusRecurso(@PathVariable Long id) {
        boolean ocupado = service.isRecursoOcupado(id);
        return java.util.Map.of("ocupado", ocupado, "status", ocupado ? "Ocupado" : "Livre");
    }

    //=== INOVAÇÃO: Endpoint REST para contar reservas do usuário ===
    @GetMapping("/api/usuario/{id}/reservas")
    @ResponseBody
    public java.util.Map<String, Object> contarReservasUsuario(@PathVariable Long id) {
        int count = service.contarReservasAtivas(id);
        int max = service.getMaxReservas();
        return java.util.Map.of("count", count, "max", max, "podeCriar", count < max);
    }

}
