package com.sistema.macroex.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    /** Método para retornar VERDADEIRO se não tiver emailo cadastrado */
    public Boolean verificarEmailExiste(String email) {

        List<Usuario> lista = repository.findAll().stream().filter(c -> c.getEmail().equals(email))
                .collect(Collectors.toList());

        return lista.isEmpty();

    }

    // Método para verificar se existe ID
    public Usuario verificarIdExiste(Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
        return usuario;
    }

    /** Métodod para salvar Usuario */
    public void salvarUsuario(Usuario usuario) {

        if (usuario.getId() != null) {
            Usuario usuarioParaPegarContrarotulo = repository.findById(usuario.getId())
                    .orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
            usuario.setContrarotulo(usuarioParaPegarContrarotulo.getContrarotulo());
        }
        /** Criptografar as senhas em perfis que não sejam distribuidor */
        if (!usuario.getPerfil().equals(Perfil.DISTRIBUIDOR)) {
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        }
        usuario.setEnable(true);

        repository.save(usuario);
    }

    public Boolean verificarUsuarioNovo(Long id) {
        return id == null;
    }

    // Buscar todos Usuarios do perfil fornecido
    public Page<Usuario> listarUsuarioPorPerfil(Perfil perfil, Pageable pageable) {
        return repository.findByPerfil(perfil, pageable);
    }

    /**
     * Para diferenciar as perfis que usaram a mesmsa pagina pelo perfil então
     * sempre pegando do perfil
     */
    public Usuario novoUsuarioComPerfil(Perfil perfil) {
        Usuario aux = new Usuario();
        aux.setPerfil(perfil);
        return aux;
    }

    /** Listar Usuarios por Perfil e nome */
    public Page<Usuario> listarUsuariosPorNomeAndPerfil(Perfil perfil, String nome, Pageable pageable) {
        return repository.findByNomeContainsIgnoreCaseAndPerfil(nome, perfil, pageable);
    }

    /** Alterar os Status */
    public void alterarEnable(Usuario usuario) {
        if (usuario.getEnable()) {
            usuario.setEnable(false);
        } else {
            usuario.setEnable(true);
        }
        repository.save(usuario);
    }

}
