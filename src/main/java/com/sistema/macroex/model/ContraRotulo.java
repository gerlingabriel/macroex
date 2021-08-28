package com.sistema.macroex.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getSafra() {
        return safra;
    }

    public void setSafra(String safra) {
        this.safra = safra;
    }

    public Paises getPais() {
        return pais;
    }

    public void setPais(Paises pais) {
        this.pais = pais;
    }

    public String getIngrediantes() {
        return ingrediantes;
    }

    public void setIngrediantes(String ingrediantes) {
        this.ingrediantes = ingrediantes;
    }

    public String getAlcoolica() {
        return alcoolica;
    }

    public void setAlcoolica(String alcoolica) {
        this.alcoolica = alcoolica;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getAdm() {
        return adm;
    }

    public void setAdm(Usuario adm) {
        this.adm = adm;
    }

    public Usuario getDistribuidor() {
        return distribuidor;
    }

    public void setDistribuidor(Usuario distribuidor) {
        this.distribuidor = distribuidor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getLiquido() {
        return liquido;
    }

    public void setLiquido(String liquido) {
        this.liquido = liquido;
    }

    

}
