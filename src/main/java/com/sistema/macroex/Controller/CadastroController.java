package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public String cadastro(HttpSession session, Model model) {

        // sempre será necessário nas paginas por causa do login
        Usuario usuario = user();
        session.setAttribute("user", usuario);

        usuario = new Usuario();

        model.addAttribute("usuario", usuario);

        return "cadastro/cadastro";
    }

    // salvado cadastro novo
    @PostMapping
    public String paginaCadastroSalvo(@Valid @ModelAttribute Usuario usuario, Model model, HttpSession session) {

        if (usuario.getNome() == null) {

            model.addAttribute("usuario", usuario);
            return "cadastro/cadastro";

        }

        usuario.setEnable(true);
        repository.save(usuario);

        var user = user();

        model.addAttribute("usuario", new Usuario());
        session.setAttribute("user", user);

        return "cadastro/cadastro";
    }

    @GetMapping(value = "/novo")
    public String novo(HttpSession session, Model model) {

        // sempre será necessário nas paginas por causa do login
        Usuario user = user();
        session.setAttribute("user", user);

        model.addAttribute("usuario", new Usuario());

        return "cadastro/cadastro";
    }

    @GetMapping(value = "/tabela")
    public String tabela(HttpSession session, Model model) {

        // sempre será necessário nas paginas por causa do login
        Usuario user = user();
        session.setAttribute("user", user);

        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("usuario", new Usuario());

        return "cadastro/tabela";
    }

    // Paginação das tabelas
    @RequestMapping(value = "/paginacao")
    public String pagiancao(Model model, @PageableDefault(size = 5) Pageable pageable, @RequestParam("nome") String nome) {

        Page<Usuario> todos = repository.findByNomeContainsIgnoreCase(nome, pageable);

        model.addAttribute("todos", todos);

        Usuario usuario = new Usuario();
        usuario.setNome(nome);

        model.addAttribute("usuario", usuario);

        return "cadastro/tabela";

    }

    @GetMapping(value = "/deletar/{id}")
    public String tabelaInativar(@PathVariable("id") Long id, Model model) {

        // Pegar o usuario com ID e inverter o enable dele
        // se é true vaificar false, se é false vai ficar true
        // por fim vai salvar a alteração
        Usuario usuario = repository.findById(id).orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
        if (usuario.getEnable()) {
            usuario.setEnable(false);
        } else {
            usuario.setEnable(true);
        }
        repository.save(usuario);

        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("usuario", new Usuario());

        var user = user();
        model.addAttribute("user", user);
        return "cadastro/tabela";

    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {

        Usuario usu = repository.findById(id).orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));

        model.addAttribute("usuario", usu);

        return "cadastro/cadastro";

    }

    // Filtrar a pesquisa pelo nome
    @GetMapping(value = "/buscar")
    public String pesquisar(@ModelAttribute Usuario usuario, Model model,@PageableDefault(size = 5) Pageable pageable) {

        String nome = usuario.getNome();
        Page<Usuario> todos = repository.findByNomeContainsIgnoreCase(nome, pageable);

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", todos);
        return "cadastro/tabela";

    }

    // Metodo para auxiliar perfil usuario
    // *********************************************************************************
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
