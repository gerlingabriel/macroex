package com.sistema.macroex.Controller;

import java.time.LocalDate;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.service.ContraRotuloService;
import com.sistema.macroex.service.JavaMailApp;
import com.sistema.macroex.service.VerificarSeTemRotulo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;


@Controller
@RequestMapping("/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class IndexController {

    private JavaMailApp javaMailApp;
    private VerificarSeTemRotulo seTemRotulo;
    private ContraRotuloService contraRotuloService;

    @GetMapping
    public String index() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/recuperar")
    public String recuperar(Model model){
        model.addAttribute("usuario", new Usuario());
        return "recuperar";
    }

    @PostMapping("/recuperar")
    public String metodoEnviarEmail(@ModelAttribute Usuario usuario, Model model){

        model.addAttribute("recuperar", "Email enviado com sucesso!");

        javaMailApp.recuoeraremail(usuario.getEmail());

        return "login";
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {

        Usuario user = contraRotuloService.user();
        session.setAttribute("user", user);
        session.setAttribute("cadastrar", seTemRotulo.verificar(contraRotuloService.user()));
        session.setAttribute("diaHoje", LocalDate.now());
        
        return "home";
    }

   

}
