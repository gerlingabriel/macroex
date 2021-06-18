package com.sistema.macroex.service;

import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommandLineAppStartupRunner implements CommandLineRunner {

    UsuarioService service;

    @Override
    public void run(String...args) throws Exception {
        if (service.verificarEmailExiste("admin@admin")) {
            Usuario usuario = new Usuario();
            usuario.setNome("Admin");
            usuario.setEmail("admin@admin");
            usuario.setSenha("123");
            usuario.setPerfil(Perfil.ADMINISTRADOR);
            service.salvarUsuario(usuario);
        }
    }
}
