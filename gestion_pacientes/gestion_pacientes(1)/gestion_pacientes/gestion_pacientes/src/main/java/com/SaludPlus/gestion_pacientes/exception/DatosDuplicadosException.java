package com.SaludPlus.gestion_pacientes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DatosDuplicadosException extends RuntimeException {
    public DatosDuplicadosException(String mensaje) {
        super(mensaje);
    }
}