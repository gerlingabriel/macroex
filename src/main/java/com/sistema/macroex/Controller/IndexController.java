package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;
import com.sistema.macroex.service.JavaMailApp;
import com.sistema.macroex.service.VerificarSeTemRotulo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private JavaMailApp javaMailApp;

    @Autowired
    private VerificarSeTemRotulo seTemRotulo;

    @GetMapping
    public String index() {

        return "index";
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

        Usuario usuario = repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        session.setAttribute("user", usuario);
        session.setAttribute("cadastrar", seTemRotulo.verificar(usuario));
        
        return "home";
    }

   

}
