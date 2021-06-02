package com.sistema.macroex.handleException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ControllerAdvice
public class HandleTratamento {

    public ResponseEntity<MsgErro> entityNotFound(ExceptionNegotion ex, HttpServletRequest request){

        MsgErro erro = new MsgErro();
        erro.setError("Recurso não encontrado");
        erro.setMsg(ex.getMessage());
        erro.setStatus(HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * InnerHandleTratamento
     */
    @Setter
    @Getter
    @NoArgsConstructor
    public class MsgErro {

        private String msg;
        private String error;
        private Integer status;
    
        
    }
    
}
