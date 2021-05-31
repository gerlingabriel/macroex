package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RotuloRepository;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/rotulo")
public class RotuloController {

    @Autowired
    private RotuloRepository rotuloRepository;

    @Autowired
    private UsuarioRepository repository;

     @GetMapping
     public String cadastroContraRotulo(Model model, @PageableDefault(size=5) Pageable pageable, HttpSession session) {
 
         Page<ContraRotulo> todos = rotuloRepository.findAll(pageable);
 
         model.addAttribute("usuariof", new Usuario());
         model.addAttribute("rotulo", new ContraRotulo());
         model.addAttribute("todos", todos);

        var user = user();
        session.setAttribute("user", user);
        
         
         return "fornecedor/rotulo";
     }

     // Metodo para auxiliar perfil usuario
    // *********************************************************************************
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    
}
