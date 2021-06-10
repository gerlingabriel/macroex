package com.sistema.macroex.Controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;

import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @GetMapping("/{perfil}")
    public String cadastro(@PathVariable("perfil") Perfil perfil, HttpSession session, Model model) {

        /**Ainda não entendi bem quando preciso setar o usuario na sessão para ficar na barra incial */
        Usuario user = user();
        session.setAttribute("user", user);

        Usuario usuario = new Usuario();
        usuario.setPerfil(perfil);
        model.addAttribute("usuario",usuario);

        return "cadastro/cadastro";
    }

    // salvado cadastro novo
    @PostMapping
    public String paginaCadastroSalvo(@Valid @ModelAttribute Usuario usuario, Model model, HttpSession session) {

        /**Verificar sem existe algum email identico o que usuario está passando */
        List<Usuario> auxUsuario = repository.findAll()
                    .stream()
                    .filter(c -> c.getEmail().equals(usuario.getEmail()))
                    .collect(Collectors.toList());

        // verificar se o email já foi cadstrado
        if (!auxUsuario.isEmpty() && usuario.getId() == null) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Email já cadstrado");

            // deixar logado
            var user = user();
            session.setAttribute("user", user);
            return "cadastro/cadastro";
        }

        /** Se tiver ID significa que é atualização, se não é cadastro novo */
        if (usuario.getId() == null) {
            model.addAttribute("salvo", "Usuario salvo com sucesso!");
        } else {
            model.addAttribute("salvo", "Usuario editado com sucesso!");
        }

        /**Criptografar as senhas em perfis que não sejam distribuidor */
        if (!usuario.getPerfil().equals(Perfil.DISTRIBUIDOR)) {
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        }        

        /** Sempre que criar ou editar o perfil será ativado - depois se quizer poderá desativar */
        usuario.setEnable(true);
        /**Salvo no banco */
        repository.save(usuario);

        //enviar dados para tabela
        Page<Usuario> todos = repository.findByPerfil(usuario.getPerfil(), PageRequest.of(0, 5, Sort.by("id")));
        model.addAttribute("todos", todos);

        /** Para diferenciar as perfis que usaram a memsa pagina pelo perfil
         * então sempre pegando do perfil
         */
        Usuario aux = new Usuario();
        aux.setPerfil(usuario.getPerfil());
        model.addAttribute("usuario",aux);

        //deixar logado
        var user = user();
        session.setAttribute("user", user);

        return "cadastro/tabela";
    }

    /** Todos Uusarios utilização a mesma pagina e para diferenciar o link indicará qual perfil acessar */
    @GetMapping(value = "/tabela/{perfil}")
    public String tabela(@PathVariable("perfil") Perfil perfil, HttpSession session, Model model) {


        Page<Usuario> todos = repository.findByPerfil(perfil, PageRequest.of(0, 5, Sort.by("id")));

        model.addAttribute("todos", todos);

        /** Para diferenciar as perfis que usaram a memsa pagina pelo perfil
         * então sempre pegando do perfil
         */
        Usuario usuario = new Usuario();
        usuario.setPerfil(perfil);
        model.addAttribute("usuario",usuario);

        return "cadastro/tabela";
    }

    // Paginação das tabelas
    @GetMapping(value = "/paginacao")
    public String pagiancao(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome,
            @RequestParam("perfil") Perfil perfil) {

        Page<Usuario> todos = null;

        /**Para não perder o nome deixei fixo o perfil Usuario, mas poderia colocarno campo somente um chave String */
        Usuario usuario = new Usuario();
        usuario.setPerfil(perfil);
        if(nome.isEmpty()){
            todos = repository.findByPerfil(perfil, pageable);
        } else {
             /** Paginação por nome */
            todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, perfil, pageable);
        }

        model.addAttribute("todos", todos);
        model.addAttribute("usuario", usuario);

        return "cadastro/tabela";
    }

    @GetMapping(value = "/deletar/{id}")
    public String tabelaInativar(@PathVariable("id") Long id, Model model) {

        Usuario usuario = verificarIdExiste(id);
        
        if (usuario.getEnable()) {
            usuario.setEnable(false);
        } else {
            usuario.setEnable(true);
        }
        repository.save(usuario);

        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("usuario", new Usuario());

        return "cadastro/tabela";
    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {
        /**Pegar o ID da tabela, pegar dados do Usuario e mandar para tela de cadastro */
        Usuario usuario = verificarIdExiste(id);

        model.addAttribute("usuario", usuario);

        return "cadastro/cadastro";
    }

    
    // Filtrar a pesquisa pelo nome
    @GetMapping(value = "/buscar")
    public String pesquisar(@ModelAttribute Usuario usuario, Model model, @PageableDefault(size = 5) Pageable pageable) {
        
        /** Se nome vier vazio volta a pesquisar somente por Perfil
         * caso tenha nome e perfim vem sempre faz a pesqusia
         */
        Page<Usuario> todos = null;
        if (usuario.getNome().isEmpty()) {
            todos = repository.findByPerfil(usuario.getPerfil(), pageable);
        } else {
            todos = repository.findByNomeContainsIgnoreCaseAndPerfil(usuario.getNome(), usuario.getPerfil(), pageable);
        }       
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", todos);
        
        return "cadastro/tabela";
    }
    
    // *********************************************************************************

    // Metodo para auxiliar perfil usuario
    private Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    //Método para verificar se existe ID
    private Usuario verificarIdExiste(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
        return usuario;
    }
}
