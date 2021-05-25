package com.sistema.macroex.Controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
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
    public String cadastroContraRotulo(Model model) {

        ContraRotulo rotulo = new ContraRotulo();

        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("fornecedores", todos);

        return "fornecedor/cadastroContraRotulo";
    }

    @RequestMapping(value = "/fornecedor/cadastroContraRotulo", method = RequestMethod.POST)
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, @ModelAttribute Usuario fornecedores, Model model) {

        ContraRotulo auxRotulo = new ContraRotulo();

        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());

        model.addAttribute("rotulo", auxRotulo);
        model.addAttribute("fornecedores", todos);

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
    public String cadastroForncedorSalvo(@ModelAttribute Usuario usuario, Model model) {

        repository.save(usuario);

        Usuario auxUsuario = new Usuario();

        Perfil tipo = Perfil.FORNECEDOR;
        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());

        model.addAttribute("usuario", auxUsuario);
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";
    }

    @RequestMapping(value = "/fornecedor/buscarFornecedor")
    public String procurar(Model model) {

        Usuario auxUsuario = new Usuario();

        List<Usuario> todos = repository.findAll().stream().filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR).collect(Collectors.toList());
        model.addAttribute("todos", todos);
        model.addAttribute("usuario", auxUsuario);

        return "/fornecedor/buscarFornecedor";
        
    }

    // Filtrar a pesquisa pelo nome
    @RequestMapping(value = "/buscar")
    public String pesquisar(@ModelAttribute Usuario uruario, Model model) {

        String nome = uruario.getNome();

        Usuario auxUsuario = new Usuario();

        List<Usuario> todos = repository.findByNomeContainsIgnoreCase(nome)
                            .stream()
                            .filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR)
                            .collect(Collectors.toList());
                 

        model.addAttribute("todos", todos);
        model.addAttribute("usuario", auxUsuario);

        return "/fornecedor/buscarFornecedor";
        
    }

    // Busca concluidae
    @RequestMapping(value = "/confirmacao")
    public String pesquisaEncotrada(@ModelAttribute Usuario uruario, Model model) {

        String nome = uruario.getNome();

        Usuario auxUsuario = new Usuario();

        List<Usuario> todos = repository.findByNomeContainsIgnoreCase(nome)
                            .stream()
                            .filter(fornecedor -> fornecedor.getPerfil() == Perfil.FORNECEDOR)
                            .collect(Collectors.toList());
                 

        model.addAttribute("todos", todos);
        model.addAttribute("usuario", auxUsuario);

        return "/fornecedor/buscarFornecedor";
        
    }

}
