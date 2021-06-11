package com.sistema.macroex.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RotuloRepository;
import com.sistema.macroex.repository.UsuarioRepository;
import com.sistema.macroex.service.JavaMailApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/rotulo")
public class RotuloController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private RotuloRepository rotuloRepository;

    @Autowired
    private JavaMailApp javaMailApp;

    @GetMapping
    public String cadastroContraRotulo(Model model, HttpSession session) {

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("rotulo", new ContraRotulo());

        var user = user();
        session.setAttribute("user", user);

        return "fornecedor/rotulo";
    }

    @GetMapping(value = "/buscarFornecedor")
    public String buscarRorulo(Model model, @PageableDefault(size = 5) Pageable pageable) {

        Perfil tipo = Perfil.FORNECEDOR;
        Page<Usuario> todos = repository.findByPerfil(tipo, pageable);

        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("todos", todos);

        return "fornecedor/buscarFornecedor";
    }

    @GetMapping(value = "/confirmacao/{id}")
    public String pesquisaEncotrada(@PathVariable("id") Long id, Model model) {

        Usuario usuario = repository.findById(id).get();

        ContraRotulo rotulo = new ContraRotulo();
        rotulo.setUsuario(usuario);

        model.addAttribute("usuariof", usuario);
        model.addAttribute("rotulo", rotulo);
        model.addAttribute("adm", false);

        return "fornecedor/rotulo";

    }

    // salvando
    @PostMapping
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, HttpSession session, Model model) {
        // Pegar o usuario logado
        var user = user();

        /** Se o perfil dele nao for ADM então irá finalizar cadastro(notificação) */
        if (isNotAdm(user)) {
            rotulo.setStatus(Status.FINALIZADO);
        } else { // Se não é o ADM cadastrando o item
            rotulo.setAdm(user);
            rotulo.setStatus(Status.CADASTRADO);
        }

        // Colocar o dia da criação e da edição se estiver vazio
        // Quando for editar irei novamente, mas quando for finalizar irei modificar a
        // data
        if (rotulo.getDataCriacao() == null || rotulo.getStatus().equals(Status.FINALIZADO)) {
            rotulo.setDataCriacao(LocalDate.now());
        }

        if (rotulo.getId() != null) {
            model.addAttribute("salvo", "Contra Rotulo editado com sucesso!");
        } else {
            model.addAttribute("salvo", "Contra Rotulo salvo com sucesso!");
        }

        try {
            /** Salvando no Usuario vai té rotulo */
            rotuloRepository.save(rotulo);
            // repository.save(auxUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Verificar para qual perfil irá ser encaminhado */
        if (isNotAdm(user)) {

            List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuario(user).stream()
                    .filter(c -> c.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

            Page<ContraRotulo> todosPaginados = new PageImpl<>(todosFiltrado, PageRequest.of(0, 5, Sort.by("id")),
                    todosFiltrado.size());

            model.addAttribute("todos", todosPaginados);
            model.addAttribute("rotulo", new ContraRotulo());
            return "fornecedor/tabelaRotulo";
        }
        /**se não for um FORNECEDOR */
        model.addAttribute("todos", rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id"))));
        model.addAttribute("rotulo", new ContraRotulo());

        return "fornecedor/tabelaRotulo";
    }

    @GetMapping(value = "/tabelaRotulo")
    public String tabelaRotulo(Model model, HttpSession session) {

        /** verificar quem está logado */
        Usuario usuario = user();
        if (isNotAdm(usuario)) {
            List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuario(usuario).stream()
                    .filter(rotulo -> rotulo.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

            Page<ContraRotulo> todosPaginados = new PageImpl<>(todosFiltrado, PageRequest.of(0, 5, Sort.by("id")),
                    todosFiltrado.size());

            model.addAttribute("todos", todosPaginados);
        } else {
            buscarTodosRotulos(model, usuario);
        }

        model.addAttribute("rotulo", new ContraRotulo());
        
        // Ver a lista
        // rotuloRepository.buscaPorUsuario(usuario).stream()
        // .filter(rotulo -> rotulo.getStatus() != Status.CADASTRADO).forEach((mostrar) -> {
        //                         System.out.println(mostrar.getAdm().getEmail());
        //                         System.out.println(mostrar.getUsuario().getEmail());
        //                     });

        return "fornecedor/tabelaRotulo";
    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = verificarExisteIdRotulo(id);

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("usuariof", rotulo.getUsuario());

        if (isNotAdm(user())) {
            model.addAttribute("notadm", true);
        }

        return "fornecedor/rotulo";

    }

    @GetMapping(value = "/notificar/{id}")
    public String tabelaNotificar(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = verificarExisteIdRotulo(id);

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("usuariof", rotulo.getUsuario());

        return "fornecedor/notificar";

    }

    // função serve somente para notificar o fornecedor sobre contro rotulo enviado
    @GetMapping(value = "/alterarstatus/{id}")
    public String tabelaAlterarStatus(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = verificarExisteIdRotulo(id);

        rotulo.setStatus(Status.NOTIFICACAO);

        rotuloRepository.save(rotulo);
        
        javaMailApp.enviaremail(rotulo.getUsuario().getEmail(), rotulo.getProduto());

        var usuario = user();

        // Se quem estiver acessao não for um ADM a pesuisa limitara somente por seus
        // rotulos
        if (isNotAdm(usuario)) {

            var todos = rotuloRepository.findByUsuario(usuario, PageRequest.of(0, 5, Sort.by("id")));

            var todosFiltrado = todos.stream().filter(c -> c.getStatus() != Status.CADASTRADO)
                    .collect(Collectors.toList());

            model.addAttribute("todos", todosFiltrado);
            model.addAttribute("rotulo", new ContraRotulo());

            return "fornecedor/tabelaRotuloFornecedor";
        }

        buscarTodosRotulos(model, usuario);

        model.addAttribute("rotulo", new ContraRotulo());
        model.addAttribute("usuariof", rotulo.getUsuario());
        model.addAttribute("salvo", "Item enviado com sucesso");

        return "fornecedor/tabelaRotulo";

    }

    // Paginação das tabelas Rotulo sem FILTRO do forcenedor
    // Essa pagição será usado pela paginção do Rotulo e
    // Pagição para cadastrar um fornecdor no rotulo novo
    @GetMapping(value = "/paginacao")
    public String pagiancaoForncedor(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome, @RequestParam("param") String tipo) {

        Page<Usuario> todos = null;
        Page<ContraRotulo> todosRotulos = null;

        // se não for ADM então é paginação de Rotulo do Fornecedor
        var user = user();
        if (user.getPerfil() != Perfil.ADMINISTRADOR) {

            /** IF -> sem nome - ELSE -> com nome na pesquisa */
            if (nome.isEmpty()) {
                /**
                 * Fazer uma lista de ContraRotulo do usuario com status diferente do cadastrado
                 * signifca que ele verá os notificado e os finalizados
                 */
                List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuario(user).stream()
                        .filter(rotulo -> rotulo.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

                /** Com a lista pronta agora é fazer paginação dessa lista */
                todosRotulos = new PageImpl<>(todosFiltrado, pageable, todosFiltrado.size());

                model.addAttribute("todos", todosRotulos);
                model.addAttribute("rotulo", new ContraRotulo());
                return "fornecedor/tabelaRotulo";

            } else {

                List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuarioETitulo(user, nome).stream()
                        .filter(rotulo -> rotulo.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

                todosRotulos = new PageImpl<>(todosFiltrado, pageable, todosFiltrado.size());

                model.addAttribute("todos", todosRotulos);
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

                todos = repository.findByPerfil(Perfil.FORNECEDOR, pageable);
                model.addAttribute("usuariof", new Usuario());
                model.addAttribute("todos", todos);
                return "fornecedor/buscarFornecedor";

            } else {

                todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, Perfil.FORNECEDOR, pageable);
                var usuariof = new Usuario();
                usuariof.setNome(nome);
                model.addAttribute("usuariof", usuariof);
                model.addAttribute("todos", todos);
                return "fornecedor/buscarFornecedor";

            }

        } else { // se não é usuario então e paginao de rotulo

            if (nome.isEmpty()) {

                todosRotulos = rotuloRepository.findAll(pageable);
                model.addAttribute("rotulo", new ContraRotulo());
                model.addAttribute("todos", todosRotulos);
                return "fornecedor/tabelaRotulo";

            } else {

                todosRotulos = rotuloRepository.findByTituloContainsIgnoreCase(nome, pageable);
                var rotulo = new ContraRotulo();
                rotulo.setTitulo(nome);
                model.addAttribute("rotulo", rotulo);
                model.addAttribute("todos", todosRotulos);
                return "fornecedor/tabelaRotulo";
            }
        }

    }

    // Paginação das tabelas Rotulo sem FILTRO do forcenedor
    @GetMapping(value = "/paginacao/fornecedor")
    public String pagiancaoRotuloFornecedor(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome) {

        var user = user();
        // Esta´Paginação é para tabela destinada para Uusario logado
        Page<ContraRotulo> todos = rotuloRepository.findByUsuarioAndTituloContainsIgnoreCase(user, nome, pageable);
        model.addAttribute("todos", todos);

        // Se for pagginção com nome o nome deve permanecer
        var rotulo = new ContraRotulo();
        rotulo.setTitulo(nome);
        model.addAttribute("rotulo", rotulo);

        return "fornecedor/buscarFornecedor";
    }

    // Filtrar a pesquisa pelo nome e sempre no perfil FORNECEDOR
    // Tem também fultrar na conta Fornecedor onde só ira aparecer itens a
    // cadastrar(notificar) e os finalizados
    @GetMapping(value = "/buscarRotulo")
    public String pesquisar(@ModelAttribute ContraRotulo rotulo, Model model,
            @PageableDefault(size = 5) Pageable pageable) {

        // Verificar se Usuario logado é um ADM ou Fornecedor
        var user = user();

        Page<ContraRotulo> todos = null;

        // Se for um forncedor deverá ter acesso somente aos Rotulo cadastrados
        if (user.getPerfil() != Perfil.ADMINISTRADOR) {

            List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuarioETitulo(user, rotulo.getTitulo())
                    .stream().filter(c -> c.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

            Page<ContraRotulo> todosPaginados = new PageImpl<>(todosFiltrado, pageable, todosFiltrado.size());

            model.addAttribute("rotulo", rotulo);
            model.addAttribute("todos", todosPaginados);
            return "fornecedor/tabelaRotulo";
        }

        /** Verificar se nome estiver vazio puxar todos */
        if (rotulo.getTitulo().isEmpty()) {
            todos = rotuloRepository.findAll(pageable);
        } else {
            todos = rotuloRepository.findByTituloContainsIgnoreCase(rotulo.getTitulo(), pageable);
        }

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("todos", todos);
        return "fornecedor/tabelaRotulo";
    }

    @PostMapping(value = "/buscarNomeFornecedor")
    public String pesquisaPorNomeForncedor(@ModelAttribute Usuario usuariof, Model model,
            @PageableDefault(size = 5) Pageable pageable) {

        Page<Usuario> todos = repository.findByNomeContainsIgnoreCaseAndPerfil(usuariof.getNome(), Perfil.FORNECEDOR,
                pageable);

        model.addAttribute("usuariof", usuariof);
        model.addAttribute("todos", todos);

        return "fornecedor/buscarFornecedor";

    }

    // Paginação das tabelas
    @GetMapping(value = "/paginacao/nome")
    public String pagiancao(Model model, @PageableDefault(size = 5) Pageable pageable,
            @RequestParam("nome") String nome) {

        Page<Usuario> todos = null;

        if (nome.isEmpty()) {
            todos = repository.findByPerfil(Perfil.FORNECEDOR, pageable);
        } else {
            /** Paginação por nome */
            todos = repository.findByNomeContainsIgnoreCaseAndPerfil(nome, Perfil.FORNECEDOR, pageable);
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

    // Metodo para auxiliar perfil usuario
    // ***********************************************************************************************************
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    // Métodod para pegar todos rotulos para tabela
    private void buscarTodosRotulos(Model model, Usuario usuario) {
        var todos = rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id")));
        model.addAttribute("todos", todos);
    }

    /** Método para verificar se existe o rotulo */
    private ContraRotulo verificarExisteIdRotulo(Long id) {
        ContraRotulo rotulo = rotuloRepository.findById(id)
                .orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
        return rotulo;
    }

    /** Verificar se usuario locago é ADM */
    private boolean isNotAdm(Usuario usuario) {
        return usuario.getPerfil() != Perfil.ADMINISTRADOR;
    }
}
