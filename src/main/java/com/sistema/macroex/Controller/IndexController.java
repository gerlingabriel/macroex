package com.sistema.macroex.Controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    @Autowired
    private UsuarioRepository repository;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping("/home")
    public String home(HttpSession session) {

        Usuario usuario = repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        session.setAttribute("usuario", usuario);

        return "home";
    }

    @RequestMapping(value = "/cadastro/cadastroFornecedor", method = RequestMethod.GET)
    public String paginaCadastro(Model model) {

        Usuario usuario = new Usuario();

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", repository.findAll());

        return "cadastro/cadastroFornecedor";
    }

    @RequestMapping(value = "/cadastro/cadastroFornecedor", method = RequestMethod.POST)
    public String paginaCadastroSalvo(@ModelAttribute Usuario usuario, Model model) {

        repository.save(usuario);

        Usuario auxUsuario = new Usuario();

        model.addAttribute("usuario", auxUsuario);
        model.addAttribute("todos", repository.findAll());

        return "cadastro/cadastroFornecedor";
    }

    @RequestMapping("/fornecedor/cadastroContraRotulo")
    public String cadastroContraRotulo() {
        return "fornecedor/cadastroContraRotulo";
    }

    @RequestMapping("/fornecedor/fornecedor")
    public String cadastroForncedor(Model model) {

        Usuario auxUsuario = new Usuario();

        Perfil tipo = Perfil.FORNECEDOR;

        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());

        model.addAttribute("usuario", auxUsuario);
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";
    }

    @RequestMapping(value = "/fornecedor/fornecedor",method = RequestMethod.POST)
    public String cadastroForncedorSlavo(@ModelAttribute Usuario usuario, Model model) {

        repository.save(usuario);

        Usuario auxUsuario = new Usuario();

        Perfil tipo = Perfil.FORNECEDOR;

        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());

        model.addAttribute("usuario", auxUsuario);
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";
    }

}
