package com.sistema.macroex.repository;

import com.sistema.macroex.model.ContraRotulo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RotuloRepository extends JpaRepository<ContraRotulo, Long>{
    
}
