package com.sistema.macroex.model;

public enum Perfil {

    USUARIO, ADMINISTRADOR;

    private String perfil;

    public String getText() {
        return perfil;
    }

    public void setText(String text) {
        this.perfil = text;
    }


}