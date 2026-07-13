package com.senai.gestao_de_espacos_corporativos.controllers;

import com.senai.gestao_de_espacos_corporativos.dtos.RecursoDto;
import com.senai.gestao_de_espacos_corporativos.dtos.ReservaDto;
import com.senai.gestao_de_espacos_corporativos.dtos.UsuarioDto;
import com.senai.gestao_de_espacos_corporativos.services.RecursoService;
import com.senai.gestao_de_espacos_corporativos.services.ReservaService;
import com.senai.gestao_de_espacos_corporativos.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PageController {

    private final UsuarioService usuarioService;
    private final RecursoService recursoService;
    private final ReservaService reservaService;

    public PageController(UsuarioService usuarioService,
                          RecursoService recursoService,
                          ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.recursoService = recursoService;
        this.reservaService = reservaService;
    }

    @GetMapping("/")
    public String getIndex() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("/home")
    public String getHome(Model model, HttpSession session) {
        model.addAttribute("usuarioLogado", session.getAttribute("usuarioLogado"));
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/usuarioinserir")
    public String getUsuarioInserir(Model model) {
        model.addAttribute("usuario", new UsuarioDto());
        return "usuarioinserir";
    }

    @GetMapping("/usuariolista")
    public String getUsuarioLista(Model model) {
        List<UsuarioDto> usuarios = usuarioService.obterListaUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "usuariolista";
    }

    @GetMapping("/usuarioatualizar/{id}")
    public String getUsuarioAtualizar(Model model, @PathVariable Long id) {
        UsuarioDto usuario = usuarioService.obterUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        return "usuarioatualizar";
    }

    @GetMapping("/recursoinserir")
    public String getRecursoInserir(Model model) {
        model.addAttribute("recurso", new RecursoDto());
        return "recursoinserir";
    }

    @GetMapping("/recursolista")
    public String getRecursoLista(Model model) {
        List<RecursoDto> recursos = recursoService.obterListaRecursos();
        model.addAttribute("recursos", recursos);
        return "recursolista";
    }

    @GetMapping("/recursoatualizar/{id}")
    public String getRecursoAtualizar(Model model, @PathVariable Long id) {
        RecursoDto recurso = recursoService.obterRecursoPorId(id);
        model.addAttribute("recurso", recurso);
        return "recursoatualizar";
    }

    @GetMapping("/reservainserir")
    public String getReservaInserir(Model model) {
        model.addAttribute("reserva", new ReservaDto());
        model.addAttribute("usuarios", usuarioService.obterListaUsuarios());
        model.addAttribute("recursos", recursoService.obterListaRecursos());
        return "reservainserir";
    }

    @GetMapping("/reservalista")
    public String getReservaLista(Model model) {
        List<ReservaDto> reservas = reservaService.obterListaReservas();
        model.addAttribute("reservas", reservas);
        return "reservalista";
    }

    @GetMapping("/reservavisualizar/{id}")
    public String getReservaVisualizar(Model model, @PathVariable Long id) {
        ReservaDto reserva = reservaService.obterReservaPorId(id);
        model.addAttribute("reserva", reserva);
        return "reservavisualizar";
    }

    @GetMapping("/reservacancelar/{id}")
    public String getReservaCancelar(Model model, @PathVariable Long id) {
        ReservaDto reserva = reservaService.obterReservaPorId(id);
        model.addAttribute("reserva", reserva);
        return "reservacancelar";
    }

}