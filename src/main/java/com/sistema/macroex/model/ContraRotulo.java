package com.sistema.macroex.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ContraRotulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String tipo;

    private String titulo;

    private String subTitulo;

    private String lote;

    private String safra;

    private Paises pais;

    @Column(columnDefinition="TEXT")
    private String ingrediantes;

    private String alcoolica;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Usuario adm;

    @ManyToOne
    private Usuario distribuidor;

    private Status status;

    private String produto;

    private LocalDate dataCriacao;

    private String liquido;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContraRotulo other = (ContraRotulo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    

}
