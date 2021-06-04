package com.sistema.macroex.Controller;

import java.time.LocalDate;

import javax.servlet.http.HttpSession;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RotuloRepository;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rotulo")
public class RotuloController {


    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private RotuloRepository rotuloRepository;

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

    // salvando
    @PostMapping
    public String cadastroContraRotuloSalvo(@ModelAttribute ContraRotulo rotulo, Model model) {

        // SALVAR O CONTRA ROTULO PARA UM FORNCEDOR
  
        rotulo.setStatus(Status.CADSTRADO);
        rotulo.setDataCriacao(LocalDate.now());

        Usuario auxUsuario = repository.findById(rotulo.getUsuario().getId()).get();

        auxUsuario.getContrarotulo().add(rotulo);


        // if (rotulo.getId() == null) {
        //     auxUsuario.getContrarotulo().add(rotulo);
        // } else {
        //     for (ContraRotulo contraRotulo : auxUsuario.getContrarotulo()) {

        //         if (contraRotulo.getId() == rotulo.getId()) {
                    
        //         }
                
        //     }
        // }
        


        try {
            repository.save(auxUsuario);            
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        //rotuloRepository.save(rotulo);
        
        model.addAttribute("usuariof", new Usuario());
        model.addAttribute("rotulo", new ContraRotulo());

        return "fornecedor/rotulo";

    } 

    @GetMapping(value = "/tabelaRotulo")
    public String tabelaRotulo(Model model, HttpSession session) {

       var todos = rotuloRepository.findAll();

       model.addAttribute("todos", todos);
       model.addAttribute("usuariof", new Usuario());

        return "fornecedor/tabelaRotulo";
    }

    @GetMapping(value = "/editar/{id}")
    public String tabelaEditar(@PathVariable("id") Long id, Model model) {

        ContraRotulo rotulo = rotuloRepository.findById(id).orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));

        model.addAttribute("rotulo", rotulo);
        model.addAttribute("usuariof", rotulo.getUsuario());

        return "fornecedor/rotulo";

    }

    

    // Metodo para auxiliar perfil usuario
    // *********************************************************************************
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
