package com.sistema.macroex.model;


public enum Status {

    CADSTRADO, NOTIFICACAO, FINALIZADO;

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
