package com.sistema.macroex.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;

import org.springframework.stereotype.Service;

@Service
public class VerificarSeTemRotulo {

    //metodo para verificar se existe rotulo para cadastrar
    public Boolean verificar(Usuario usuario) {
    
    List<ContraRotulo> listaContraRotuloUsuario = usuario.getContrarotulo()
        .stream()
        .filter(rot -> rot.getStatus().equals(Status.NOTIFICACAO))
        .collect(Collectors.toList());

    if (listaContraRotuloUsuario.isEmpty()) {
        return false;
    } 
    return true;
    }

}
