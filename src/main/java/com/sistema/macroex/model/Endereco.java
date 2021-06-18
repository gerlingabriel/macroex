package com.sistema.macroex.model;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Endereco {

    private String cep;

    private String rua;

    private String complemento;

    private String bairro;

    private String cidade;

    private String uf;

}
