package com.SaludPlus.gestion_pacientes.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNoEncontradoException ex) {
        return build(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage());
    }

    @ExceptionHandler(DatosDuplicadosException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(DatosDuplicadosException ex) {
        return build(HttpStatus.CONFLICT, "Dato duplicado", ex.getMessage());
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(ValidacionException ex) {
        return build(HttpStatus.BAD_REQUEST, "Error de validación", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, "Campos inválidos", errores);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleSqlDuplicado(SQLIntegrityConstraintViolationException ex) {
        return build(HttpStatus.CONFLICT, "Dato duplicado",
                "El RUN o el correo ingresado ya existe en el sistema.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Conflicto en base de datos",
                "Ya existe un registro con ese dato único (RUN o correo).");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado. Contacte al administrador.");
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String error, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", error);
        body.put("mensaje", mensaje);
        return new ResponseEntity<>(body, status);
    }
}