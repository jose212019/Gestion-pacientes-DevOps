package com.SaludPlus.gestion_pacientes.dto;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}