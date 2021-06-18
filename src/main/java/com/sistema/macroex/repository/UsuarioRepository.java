package com.sistema.macroex.repository;

import java.util.Collection;
import java.util.List;

import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    @Query("select u from Usuario u where u.email = ?1")
	Usuario findByUsuarioByLogin(String login);

    Usuario findByEmail(String name);

    Collection<Usuario> findByNomeContainsIgnoreCase (String nome);

    Page<Usuario> findByNomeContainsIgnoreCase (String nome, Pageable pageable);

    Page<Usuario> findByPerfil (Perfil perfil, Pageable pageable);

    Page<Usuario> findByNomeContainsIgnoreCaseAndPerfil(String nome, Perfil tipo, Pageable pageable);

    List<Usuario> findByPerfil(Perfil perfil);

    
}
