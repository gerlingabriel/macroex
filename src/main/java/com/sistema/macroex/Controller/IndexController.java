package com.sistema.macroex.Controller;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    //Pagina cadstro
    @RequestMapping(value = "/cadastro/cadastroFornecedor", method = RequestMethod.GET)
    public String paginaCadastro(Model model) {

        Usuario usuario = new Usuario();

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

        return "cadastro/cadastroUsuario";
    }

    //salvado cadastro novo
    @RequestMapping(value = "/cadastro/cadastroFornecedor", method = RequestMethod.POST)
    public String paginaCadastroSalvo(@ModelAttribute Usuario usuario, Model model) {

        if (usuario.getPerfil() == null) {
            usuario.setPerfil(Perfil.ADMINISTRADOR);            
        }
        
        repository.save(usuario);

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

        return "cadastro/cadastroUsuario";
    }

    // Paginação das tabelas
    @RequestMapping(value = "/paginacao")
    public String pagiancao(Model model, @PageableDefault(size=5) Pageable pageable){

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todos", repository.findAll(pageable));



        return "cadastro/cadastroUsuario";

    } //FIM **************************************************************************************

    // CADASTRADNDO ROTUTO **********************************************************************
    @RequestMapping("/fornecedor/cadastroContraRotulo")
    public String cadastroContraRotulo(Model model) {

        model.addAttribute("rotulo", new ContraRotulo());

        return "fornecedor/cadastroContraRotulo";
    }

    @RequestMapping(value = "/fornecedor/cadastroContraRotulo", method = RequestMethod.POST)
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, @ModelAttribute Usuario fornecedores, Model model) {

        // AINDA COLOCAR METODOS PARA CADASTRAR ROTULO

        model.addAttribute("rotulo", new ContraRotulo());
        model.addAttribute("usuariof", new Usuario()); 

        return "fornecedor/cadastroContraRotulo";
    } //FIM *************************************************************************************************************************

    // CADSTRANDO FORNECEDOR *****************************************************************
    @RequestMapping("/fornecedor/fornecedor")
    public String cadastroForncedor(Model model, @PageableDefault(size=5) Pageable pageable) {

        Perfil tipo = Perfil.FORNECEDOR;

        Page<Usuario> todos = repository.findByPerfil(tipo,pageable);
       

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";
    }

    @RequestMapping(value = "/fornecedor/fornecedor",method = RequestMethod.POST)
    public String cadastroForncedorSalvo(@ModelAttribute Usuario usuario, Model model, @PageableDefault(size=5) Pageable pageable) {

        repository.save(usuario);

        Perfil tipo = Perfil.FORNECEDOR;
        Page<Usuario> todos = repository.findByPerfil(tipo,pageable);

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";
    } //FIM **********************************************************************************************************

    // requisição que chama busca por fornecedores (entrar na página)
    @RequestMapping(value = "/fornecedor/buscarFornecedor")
    public String procurar(@ModelAttribute Usuario usuario, Model model) {
 
        Perfil tipo = Perfil.FORNECEDOR;
        String nome ="";

        if (usuario.getNome() != null ){
            nome = usuario.getNome();
        }

        Page<Usuario> todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, tipo, PageRequest.of(0, 5, Sort.by("id")));
        
        model.addAttribute("todos", todos);
        model.addAttribute("usuario", new Usuario());

        return "fornecedor/buscarFornecedor";
        
    }

    // Paginação das tabelas - buscar FORNCEDOR (páginacao)
    @RequestMapping(value = "/paginacao/fornecedor")
    public String pagiancaoFornecedor(@PageableDefault(size=5) Pageable pageable, @ModelAttribute Usuario usuario, Model model){

        Perfil tipo = Perfil.FORNECEDOR;
        String nome ="";

        if (usuario.getNome() != null ){
            nome = usuario.getNome();
        } 

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("todos", repository.findByNomeContainsIgnoreCaseAndPerfil(nome, tipo, pageable));
        model.addAttribute("rotulo", new ContraRotulo());

       return "fornecedor/buscarFornecedor";

    }

    // Filtrar a pesquisa pelo nome
    @RequestMapping(value = "/buscar")
    public String pesquisar(@ModelAttribute Usuario usuario, Model model, @PageableDefault(size=5) Pageable pageable) {

        String nome = usuario.getNome();

        Perfil tipo = Perfil.FORNECEDOR;
        Page<Usuario> todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, tipo, pageable);                 

        model.addAttribute("todos", todos);
        model.addAttribute("usuario", new Usuario());
        return "fornecedor/buscarFornecedor";
        
    }

    // Busca concluidae
    @RequestMapping(value = "/confirmacao/{id}")
    public String pesquisaEncotrada(@PathVariable ("id") Long id, Model model) {

        Usuario usu = repository.findById(id).get();

        model.addAttribute("rotulo", new ContraRotulo());
        model.addAttribute("usuariof", usu);     

        return "fornecedor/cadastroContraRotulo";
        
    } // ************************************************************************************************************************************

    @RequestMapping(value = "cadastrousuario/deletar/{id}")
    public String cadastroUsuarioDeletar(@PathVariable ("id") Long id, Model model) {

        repository.deleteById(id);

        Usuario usuario = new Usuario();

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

        return "cadastro/cadastroUsuario";
        
    }

    @RequestMapping(value = "cadastrousuario/editar/{id}")
    public String cadastroUsuarioEditar(@PathVariable ("id") Long id, Model model) {

       Usuario usu = repository.findById(id).get();

        model.addAttribute("usuario", usu);
        model.addAttribute("todos", repository.findAll(PageRequest.of(0, 5, Sort.by("id"))));

        return "cadastro/cadastroUsuario";
        
    }


}
