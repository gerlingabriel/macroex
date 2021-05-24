package com.sistema.macroex.repository;

import com.sistema.macroex.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    @Query("select u from Usuario u where u.email = ?1")
	Usuario findByUsuarioByLogin(String login);

    Usuario findByEmail(String name);
    
}
