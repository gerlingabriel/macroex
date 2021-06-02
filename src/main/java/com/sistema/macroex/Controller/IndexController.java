package com.sistema.macroex.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    private UsuarioRepository repository;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {

        Usuario usuario = repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        session.setAttribute("user", usuario);

        List<ContraRotulo> todos = new ArrayList<>();

        for (Usuario usuario2 : repository.findAll()) {
            if (usuario2.getId() == usuario.getId()) {
                for (ContraRotulo rotulo : usuario2.getContrarotulo()) {
                    todos.add(rotulo);
                }
            }            
        }

        model.addAttribute("todos", todos);
        
        return "home";
    }

   

}
