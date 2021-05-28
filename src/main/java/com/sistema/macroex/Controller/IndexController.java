package com.sistema.macroex.Controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RotuloRepository;
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

    @Autowired
    private RotuloRepository rotuloRepository;

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
        session.setAttribute("usuario", usuario);

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

    } //FIM *******************************************************************************************

    // CADASTRADNDO ROTUTO *****************************************************************************
    @RequestMapping("/fornecedor/cadastroContraRotulo")
    public String cadastroContraRotulo(Model model, @PageableDefault(size=5) Pageable pageable) {

        Page<ContraRotulo> todos = rotuloRepository.findAll(pageable);

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("todos", todos);
        model.addAttribute("rotulo", new ContraRotulo());
        
        return "fornecedor/cadastroContraRotulo";
    }

    // salvando
    @RequestMapping(value = "/fornecedor/cadastroContraRotulo", method = RequestMethod.POST)
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, Model model, @PageableDefault(size=5) Pageable pageable) {

        // SALVAR O CONTRA ROTULO PARA UM FORNCEDOR
  
        rotulo.setStatus(Status.PENDENTE);

        Usuario auxUsuario = repository.findById(rotulo.getUsuario().getId()).get();
        auxUsuario.getContrarotulo().add(rotulo);

        repository.save(auxUsuario);
        
        //rotuloRepository.save(rotulo);

        Page<ContraRotulo> todos = rotuloRepository.findAll(pageable);

        model.addAttribute("todos", todos);
        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("rotulo", new ContraRotulo());

        return "fornecedor/cadastroContraRotulo";

    } 
    
    @RequestMapping("/paginacao/rotulo")
    public String cadastroContraRotuloPaginacao(@ModelAttribute Usuario usuariof, Model model, @PageableDefault(size=5) Pageable pageable) {

        Page<ContraRotulo> todos = rotuloRepository.findAll(pageable);

        model.addAttribute("usuariof", usuariof);
        model.addAttribute("todos", todos);

        return "fornecedor/cadastroContraRotulo";

    }
    @RequestMapping(value = "rotulo/deletar/{id}")
    public String paginacaoRotuloEditar(@PathVariable ("id") Long id, Model model) {

        rotuloRepository.deleteById(id);

        Usuario usuario = new Usuario();
        ContraRotulo rotulo = new ContraRotulo();

        model.addAttribute("usuariof", usuario);
        model.addAttribute("todos", rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("rotulo", rotulo);

        return "fornecedor/cadastroContraRotulo";
        
    }

    @RequestMapping(value = "rotulo/editar/{id}")
    public String paginacaoRotuloDeletar(@PathVariable ("id") Long id, Model model) {

       ContraRotulo rotulo = rotuloRepository.findById(id).get();

       Usuario usuariof = repository.findById(rotulo.getUsuario().getId()).get();

       model.addAttribute("todos", rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
       model.addAttribute("rotulo", rotulo);
       model.addAttribute("usuariof", usuariof);

        return "fornecedor/cadastroContraRotulo";
        
    }  
    
     // requisição que chama busca por fornecedores (entrar na página)
     @RequestMapping(value = "/fornecedor/buscarFornecedor")
     public String procurar(@ModelAttribute Usuario usuario, Model model) {
  
         Perfil tipo = Perfil.FORNECEDOR;
         String nome = "";
 
         if (usuario.getNome() != null ){
             nome = usuario.getNome();
         }
 
         Page<Usuario> todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, tipo, PageRequest.of(0, 5, Sort.by("id")));
         
         model.addAttribute("todos", todos);
         model.addAttribute("usuariof", usuario);
 
         return "fornecedor/buscarFornecedor";
         
     }

      // Busca concluida MARCAR COMO ACHADO O FORNECEDOR *************************************************************************
    @RequestMapping(value = "/confirmacao/{id}")
    public String pesquisaEncotrada(@PathVariable ("id") Long id, Model model, @PageableDefault(size=5) Pageable pageable) {

        Usuario usuario = repository.findById(id).get();
        Page<ContraRotulo> todos = rotuloRepository.findAll(pageable);
        ContraRotulo rotulo = new ContraRotulo();
        rotulo.setUsuario(usuario);

        model.addAttribute("usuariof", usuario);
        model.addAttribute("todos", todos);
        model.addAttribute("rotulo", rotulo);

        return "fornecedor/cadastroContraRotulo";
        
    }//FIM ************************************************************************************************************************
    //FIM *************************************************************************************************************************

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

    }

    @RequestMapping(value = "/paginacao/cadastrof")
    public String cadastroForncedorPaginacao(@ModelAttribute Usuario usuario, Model model, @PageableDefault(size=5) Pageable pageable) {

        Perfil tipo = Perfil.FORNECEDOR;
        Page<Usuario> todos = repository.findByPerfil(tipo,pageable);

        model.addAttribute("usuario", usuario);
        model.addAttribute("todos", todos);
        model.addAttribute("fornecedor", tipo);

        return "fornecedor/fornecedor";

    } //FIM **********************************************************************************************************


    // Paginação das tabelas - buscar FORNCEDOR (páginacao)
    @RequestMapping(value = "/paginacao/fornecedor")
    public String pagiancaoFornecedor(@PageableDefault(size=5) Pageable pageable, @ModelAttribute Usuario usuario, Model model){

        Perfil tipo = Perfil.FORNECEDOR;
        String nome ="";

        if (usuario.getNome() != null ){
            nome = usuario.getNome();
        } 

        model.addAttribute("usuariof", new Usuario());
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
