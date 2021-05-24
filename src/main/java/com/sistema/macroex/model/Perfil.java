package com.sistema.macroex.model;

public enum Perfil {

    FUNCIONARIO, ADMINISTRADOR, FORNECEDOR;

    private String perfil;

    public String getText() {
        return perfil;
    }

    public void setText(String text) {
        this.perfil = text;
    }


}