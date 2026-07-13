package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.services.RecursoService;
import com.senai.gestao_de_espacos_corporativos.services.ReservaService;
import com.senai.gestao_de_espacos_corporativos.services.UsuarioService;
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

import java.util.List;

@Controller
public class ReservaController {

    private final ReservaService service;
    private final UsuarioService usuarioService;
    private final RecursoService recursoService;

    public ReservaController(ReservaService service, UsuarioService usuarioService, RecursoService recursoService) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.recursoService = recursoService;
    }

    // ============================================================
    // CRIAR RESERVA
    // Recebe os dados do formulário, valida e salva.
    // Se der erro, volta pro form com as listas de usuários/recursos.
    // ============================================================
    @PostMapping("/reservainserir")
    public String cadastrarReserva(@Valid @ModelAttribute("reserva")ReservaDto reservaDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){
        // erro de bean validation (campo obrigatório vazio)
        if (bindingResult.hasErrors()){
            adicionarListasAoModel(model);
            return "reservainserir";
        }
        try {
            service.inserirReserva(reservaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Reserva cadastrada com sucesso.");
            return "redirect:/reservalista";
        } catch (RuntimeException e) {
            // erro de regra de negócio (conflito, limite, etc)
            model.addAttribute("erro", e.getMessage());
            adicionarListasAoModel(model); // repopula os dropdowns
            return "reservainserir";
        }
    }

    // ============================================================
    // CANCELAR RESERVA
    // Recebe os dados do formulário de cancelamento.
    // Exige observação obrigatória.
    // ============================================================
    @PostMapping("/reservacancelar")
    public String cancelarReserva(@Valid @ModelAttribute("reserva") ReservaDto reservaDto, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "reservacancelar";
        }
        try {
            service.cancelarReserva(reservaDto);
            redirectAttributes.addFlashAttribute("mensagem", "Reserva cancelada com sucesso.");
            return "redirect:/reservalista";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "reservacancelar";
        }
    }

    // ============================================================
    // EXCLUIR RESERVA
    // Endpoint chamado pelo JavaScript (fetch + DELETE).
    // Retorna mensagem de sucesso ou erro em texto.
    // ============================================================
    @DeleteMapping("/reservaexcluir/{id}")
    public ResponseEntity<String> excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            service.excluir(id);
            return ResponseEntity.ok().body("Excluido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================================================
    // APIs REST - usadas pelo JavaScript nas telas
    // ============================================================

    // retorna os horários do recurso (innovação: horário automático)
    @GetMapping("/api/recurso/{id}/horarios")
    @ResponseBody
    public RecursoDto obterHorariosRecurso(@PathVariable Long id) {
        return service.obterHorariosRecurso(id);
    }

    // retorna se o recurso tá ocupado agora (innovação: indicador visual)
    @GetMapping("/api/recurso/{id}/status")
    @ResponseBody
    public java.util.Map<String, Object> obterStatusRecurso(@PathVariable Long id) {
        boolean ocupado = service.isRecursoOcupado(id);
        return java.util.Map.of("ocupado", ocupado, "status", ocupado ? "Ocupado" : "Livre");
    }

    // retorna quantas reservas ativas o usuário tem (innovação: limite)
    @GetMapping("/api/usuario/{id}/reservas")
    @ResponseBody
    public java.util.Map<String, Object> contarReservasUsuario(@PathVariable Long id) {
        int count = service.contarReservasAtivas(id);
        int max = service.getMaxReservas();
        return java.util.Map.of("count", count, "max", max, "podeCriar", count < max);
    }

    // método auxiliar: repopula os dropdowns quando dá erro no form
    private void adicionarListasAoModel(Model model) {
        List<UsuarioDto> usuarios = usuarioService.obterListaUsuarios();
        List<RecursoDto> recursos = recursoService.obterListaRecursos();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("recursos", recursos);
    }

}
