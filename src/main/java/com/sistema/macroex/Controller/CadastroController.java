package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;

import com.sistema.macroex.service.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * As três classes vão utilizar essa página, então setar sempre o perfil é
     * importante
     */
    @GetMapping("/{perfil}")
    public String cadastro(@PathVariable("perfil") Perfil perfil, Model model) {

        model.addAttribute("usuario", usuarioService.novoUsuarioComPerfil(perfil));
        return "cadastro/cadastro";
    }

    // salvado cadastro novo
    @PostMapping
    public String paginaCadastroSalvo(@Valid @ModelAttribute Usuario usuario, Model model) {

        // verificar se o email já foi cadstrado
        if (!usuarioService.verificarEmailExiste(usuario.getEmail()) && usuario.getId() == null) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Email já cadstrado");
            //Retornar para página de cadstro
            return "cadastro/cadastro";
        }

        /** Abaixo setar valores para página */
        /** Se tiver ID significa que é atualização, se não é cadastro novo */
        if (usuarioService.verificarUsuarioNovo(usuario.getId())) {
            model.addAttribute("salvo", "Usuario salvo com sucesso!");
        } else {
            model.addAttribute("salvo", "Usuario editado com sucesso!");
        } 
        
        usuarioService.salvarUsuario(usuario);

        model.addAttribute("todos", usuarioService.listarUsuarioPorPerfil(usuario.getPerfil(), PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("usuario", usuarioService.novoUsuarioComPerfil(usuario.getPerfil()));
        return "cadastro/tabela";
    }

    /**
     * Todos Uusarios utilização a mesma pagina e para diferenciar o link indicará
     * qual perfil acessar
     */
    @GetMapping(value = "/tabela/{perfil}")
    public String tabela(@PathVariable("perfil") Perfil perfil, HttpSession session, Model model) {

        model.addAttribute("todos", usuarioService.listarUsuarioPorPerfil(perfil, PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("usuario", usuarioService.novoUsuarioComPerfil(perfil));
        return "cadastro/tabela";
    }

    // Paginação das tabelas
    @GetMapping(value = "/paginacao")
    public String pagiancao(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome,
            @RequestParam("perfil") Perfil perfil) {

        Page<Usuario> todos = null;

        if (nome.isEmpty()) {
            todos = usuarioService.listarUsuarioPorPerfil(perfil, pageable);
        } else {
            todos = usuarioService.listarUsuariosPorNomeAndPerfil(perfil, nome, pageable);
        }

        model.addAttribute("todos", todos);
        model.addAttribute("usuario", usuarioService.novoUsuarioComPerfil(perfil));
        return "cadastro/tabela";
    }

    @GetMapping(value = "/deletar")
    public String tabelaInativar(@RequestParam("id") Long id, Model model, @PageableDefault(size = 5) Pageable pageable) {
        
        Usuario usuario = usuarioService.verificarIdExiste(id); 

        usuarioService.alterarEnable(usuario);

        model.addAttribute("todos", usuarioService.listarUsuarioPorPerfil(usuario.getPerfil(), pageable));
        model.addAttribute("usuario", usuarioService.novoUsuarioComPerfil(usuario.getPerfil()));
        return "cadastro/tabela";
    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {
        /**
         * Pegar o ID da tabela, pegar dados do Usuario e mandar para tela de cadastro
         */
        Usuario usuario = usuarioService.verificarIdExiste(id);

        model.addAttribute("usuario", usuario);
        return "cadastro/cadastro";
    }

    // Filtrar a pesquisa pelo nome
    @GetMapping(value = "/buscar")
    public String pesquisar(@ModelAttribute Usuario usuario, Model model,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<Usuario> todos = null;

        //Verificar se nome estiver vazio
        if (usuario.getNome().isEmpty()) {
            todos = usuarioService.listarUsuarioPorPerfil(usuario.getPerfil(), PageRequest.of(0, 5, Sort.by("id")));
        } else {
            todos = usuarioService.listarUsuariosPorNomeAndPerfil(usuario.getPerfil(), usuario.getNome(), pageable);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", todos);

        return "cadastro/tabela";
    }

}