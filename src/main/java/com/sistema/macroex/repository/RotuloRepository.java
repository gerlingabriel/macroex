package com.sistema.macroex.repository;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RotuloRepository extends JpaRepository<ContraRotulo, Long>{

    Page<ContraRotulo> findByTituloContainsIgnoreCase(String nome, Pageable pageable);

    Page<ContraRotulo> findByUsuario(Usuario usuario, Pageable pageable);

    Page<ContraRotulo> findByUsuarioAndTituloContainsIgnoreCase(Usuario usuario, String nome, Pageable pageable);
    
}
