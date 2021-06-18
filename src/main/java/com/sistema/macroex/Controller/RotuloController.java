package com.sistema.macroex.Controller;

import java.time.LocalDate;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.service.ContraRotuloService;
import com.sistema.macroex.service.JavaMailApp;
import com.sistema.macroex.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/rotulo")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RotuloController {

    private ContraRotuloService contraRotuloService;

    private UsuarioService usuarioService;

    private final JavaMailApp javaMailApp;

    @GetMapping
    public String cadastroContraRotulo(Model model) {

        model.addAttribute("usuariof", usuarioService.listaTodosFornecedoresSelecet());
        model.addAttribute("rotulo", new ContraRotulo());
        return "fornecedor/rotulo";
    }

    @GetMapping(value = "/buscarFornecedor")
    public String buscarRorulo(Model model, @PageableDefault(size = 5) Pageable pageable) {

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("todos", contraRotuloService.buscarTodosFornecedores(pageable));
        return "fornecedor/buscarFornecedor";
    }

    @GetMapping(value = "/confirmacao/{id}")
    public String pesquisaEncotrada(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = new ContraRotulo();
        rotulo.setUsuario(usuarioService.verificarIdExiste(id));

        model.addAttribute("usuariof", usuarioService.verificarIdExiste(id));
        model.addAttribute("rotulo", rotulo);
        model.addAttribute("adm", false);

        return "fornecedor/rotulo";
    }

    /** salvando */
    @PostMapping
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, Model model) {
        // Pegar o usuario logado
        var user = contraRotuloService.user();

        /** Se o perfil dele nao for ADM então irá finalizar cadastro(notificação) */
        if (contraRotuloService.isNotAdm(user)) {
            rotulo.setStatus(Status.FINALIZADO);
            rotulo.setDataCriacao(LocalDate.now());
        } else { // Se não é o ADM cadastrando o item

            // Verificar é criação
            //se for será criado
            //se não não mudará o Autor da Ccriação e nem data
            if (contraRotuloService.verificarSeRotuloFoiCadastradp(rotulo)) {
                rotulo.setAdm(user);
                rotulo.setStatus(Status.CADASTRADO);
                rotulo.setDataCriacao(LocalDate.now());
            }
           
        }

        if (rotulo.getId() != null) {
            model.addAttribute("salvo", "Contra Rotulo editado com sucesso!");
        } else {
            model.addAttribute("salvo", "Contra Rotulo salvo com sucesso!");
        }

        try {
            /** Salvando no Usuario vai té rotulo */
            contraRotuloService.salvarRotulo(rotulo);
            // repository.save(auxUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Verificar para qual perfil irá ser encaminhado */
        if (contraRotuloService.isNotAdm(user)) {

            model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaParaPage(user));
            model.addAttribute("rotulo", new ContraRotulo());
            return "fornecedor/tabelaRotulo";
        }
        /**se não for um FORNECEDOR */
        model.addAttribute("todos", contraRotuloService.ListarTodosUsuarios());
        model.addAttribute("rotulo", new ContraRotulo());

        return "fornecedor/tabelaRotulo";
    }

    @GetMapping(value = "/tabelaRotulo")
    public String tabelaRotulo(Model model) {

        Usuario user = contraRotuloService.user();

        if (contraRotuloService.isNotAdm(user)) {
            model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaParaPage(user));
        } else { //ADM
            model.addAttribute("todos", contraRotuloService.buscarTodosRotulos());
        }

        model.addAttribute("rotulo", new ContraRotulo());
        return "fornecedor/tabelaRotulo";
    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = contraRotuloService.verificarExisteIdRotulo(id);

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("usuariof", rotulo.getUsuario());

        if (contraRotuloService.isNotAdm(contraRotuloService.user())) {
            model.addAttribute("notadm", true);
        }

        return "fornecedor/rotulo";
    }

    @GetMapping(value = "/notificar/{id}")
    public String tabelaNotificar(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = contraRotuloService.verificarExisteIdRotulo(id);

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("usuariof", rotulo.getUsuario());

        return "fornecedor/notificar";

    }

    // função serve somente para notificar o fornecedor sobre contro rotulo enviado
    @GetMapping(value = "/alterarstatus/{id}")
    public String tabelaAlterarStatus(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = contraRotuloService.verificarExisteIdRotulo(id);

        rotulo.setStatus(Status.NOTIFICACAO);

        contraRotuloService.salvarRotulo(rotulo);
        
        javaMailApp.enviaremail(rotulo.getUsuario().getEmail(), rotulo.getProduto());

        var user = contraRotuloService.user();

        // Se quem estiver acessao não for um ADM a pesuisa limitara somente por seus
        // rotulos
        if (contraRotuloService.isNotAdm(user)) {

            model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaParaPage(user));
            model.addAttribute("rotulo", new ContraRotulo());

            return "fornecedor/tabelaRotuloFornecedor";
        }
        // Lista atributos para usuario ADM
        model.addAttribute("todos", contraRotuloService.buscarTodosRotulos());
        model.addAttribute("rotulo", new ContraRotulo());
        model.addAttribute("salvo", "Item enviado com sucesso");
        
        return "fornecedor/tabelaRotulo";
        //model.addAttribute("usuariof", rotulo.getUsuario());

    }

    // Paginação das tabelas Rotulo sem FILTRO do forcenedor
    // Essa pagição será usado pela paginção do Rotulo e
    // Pagição para cadastrar um fornecdor no rotulo novo
    @GetMapping(value = "/paginacao")
    public String pagiancaoForncedor(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome, 
            @RequestParam("param") String tipo) {

        // se não for ADM então é paginação de Rotulo do Fornecedor
        var user = contraRotuloService.user();
        
        if (contraRotuloService.isNotAdm(user)) {

            // IF -> sem nome ***********************
            if (nome.isEmpty()) {

                model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaParaPage(user));
                model.addAttribute("rotulo", new ContraRotulo());
                return "fornecedor/tabelaRotulo";

            } else {// ELSE -> com nome na pesquisa */

                model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaPorTituloParaPage(user, nome, pageable));
                var rotulo = new ContraRotulo();
                rotulo.setTipo(nome);
                model.addAttribute("rotulo", rotulo);
                return "fornecedor/tabelaRotulo";

            }

        } // fim da Condição do usuario FORNECEDOR

        /** Aqui será a PAGINAÇÂO ADMINISTRADOR */
        /** Se a pagiação for na pesquisa de fornecedor para ADD rotulo */
        if (tipo.equals("usuario")) {

            if (nome.isEmpty()) {
                
                model.addAttribute("usuariof", new Usuario());
                model.addAttribute("todos", contraRotuloService.buscarTodosFornecedores(pageable));
                return "fornecedor/buscarFornecedor";
            } else {

                var usuariof = new Usuario();
                usuariof.setNome(nome);
                model.addAttribute("usuariof", usuariof);
                model.addAttribute("todos", contraRotuloService.buscarFornecedoresFiltradoPorNOme(nome, pageable));
                return "fornecedor/buscarFornecedor";
            }

        } else { 
            // Esse get paginação é usando por duas pagianção
            // se não é usuario então e paginao de rotulo

            if (nome.isEmpty()) {

                model.addAttribute("rotulo", new ContraRotulo());
                model.addAttribute("todos", contraRotuloService.buscarTodosRotulos(pageable));
                return "fornecedor/tabelaRotulo";
            } else {

                var rotulo = new ContraRotulo();
                rotulo.setTitulo(nome);
                model.addAttribute("rotulo", rotulo);
                model.addAttribute("todos", contraRotuloService.busscarTodosRotulosFiltroNome(nome, pageable));
                return "fornecedor/tabelaRotulo";
            }
        }

    }

    /** Paginação das tabelas Rotulo sem FILTRO do forcenedor */
    // @GetMapping(value = "/paginacao/fornecedor")
    // public String pagiancaoRotuloFornecedor(Model model, @PageableDefault(size = 5) Pageable pageable,
    //         @RequestParam("nome") String nome) {

    //     var user = contraRotuloService.user();

    //     model.addAttribute("todos",  contraRotuloService.buscarUsuarioFiltroTitulo(user, nome, pageable));

    //     // Se for pagginção com nome o nome deve permanecer
    //     var rotulo = new ContraRotulo();
    //     rotulo.setTitulo(nome);
    //     model.addAttribute("rotulo", rotulo);

    //     return "fornecedor/buscarFornecedor";
    // }

    // Filtrar a pesquisa pelo nome e sempre no perfil FORNECEDOR
    // Tem também filtrar na conta Fornecedor onde só ira aparecer itens a
    // cadastrar (notificar) e os finalizados
    @GetMapping(value = "/buscarRotulo")
    public String pesquisar(@ModelAttribute ContraRotulo rotulo, Model model,
            @PageableDefault(size = 5) Pageable pageable) {

        // Verificar se Usuario logado é um ADM ou Fornecedor
        var user = contraRotuloService.user();

        Page<ContraRotulo> todos = null;

        // Se for um forncedor deverá ter acesso somente aos Rotulo cadastrados
        if (contraRotuloService.isNotAdm(user)) {

            model.addAttribute("rotulo", rotulo);
            model.addAttribute("todos", contraRotuloService.filtroDaListaExpecificaPorTituloParaPage(user, rotulo.getTitulo(), pageable));
            return "fornecedor/tabelaRotulo";
        }

        // Abaixo acesso aos ADM (funcionários)
        // Verificar se nome estiver vazio puxar todos 
        if (rotulo.getTitulo().isEmpty()) {
            todos = contraRotuloService.buscarTodosRotulos(pageable);
        } else {
            todos = contraRotuloService.busscarTodosRotulosFiltroNome(rotulo.getTitulo(), pageable);
        }

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("todos", todos);
        return "fornecedor/tabelaRotulo";
    }

    @PostMapping(value = "/buscarNomeFornecedor")
    public String pesquisaPorNomeForncedor(@ModelAttribute Usuario usuariof, Model model,
            @PageableDefault(size = 5) Pageable pageable) {
                
        model.addAttribute("usuariof", usuariof);
        model.addAttribute("todos", usuarioService.listarUsuariosPorNomeAndPerfil(Perfil.FORNECEDOR, usuariof.getNome(), pageable));

        return "fornecedor/buscarFornecedor";

    }

    // Paginação das tabelas
    @GetMapping(value = "/paginacao/nome")
    public String pagiancao(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome) {

        Page<Usuario> todos = null;

        if (nome.isEmpty()) {
            todos = usuarioService.listarUsuarioPorPerfil(Perfil.FORNECEDOR, pageable);
        } else {
            /** Paginação por nome */
            todos = usuarioService.listarUsuariosPorNomeAndPerfil(Perfil.FORNECEDOR, nome, pageable);
        }

        /**
         * Para não perder o nome deixei fixo o perfil Usuario, mas poderia colocarno
         * campo somente um chave String
         */
        var usuario = new Usuario();
        usuario.setNome(nome);
        model.addAttribute("todos", todos);
        model.addAttribute("usuariof", usuario);

        return "cadastro/tabela";
    }




  
}
