package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rotulo")
public class RotuloController {


    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public String cadastroContraRotulo(Model model, HttpSession session) {

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("rotulo", new ContraRotulo());

        var user = user();
        session.setAttribute("user", user);

        return "fornecedor/rotulo";
    }

    @GetMapping(value = "/buscarFornecedor")
    public String buscarRorulo (Model model, @PageableDefault(size=5) Pageable pageable) {

        Perfil tipo = Perfil.FORNECEDOR;
        Page<Usuario> todos = repository.findByPerfil(tipo,pageable);

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("todos", todos);

        return "fornecedor/buscarFornecedor";
    }

    @GetMapping(value = "/confirmacao/{id}")
    public String pesquisaEncotrada(@PathVariable ("id") Long id, Model model) {

        Usuario usuario = repository.findById(id).get();

        ContraRotulo rotulo = new ContraRotulo();
        rotulo.setUsuario(usuario);

        model.addAttribute("usuariof", usuario);
        model.addAttribute("rotulo", rotulo);

        return "fornecedor/rotulo";
        
    }


    

    // Metodo para auxiliar perfil usuario
    // *********************************************************************************
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
