package com.SaludPlus.gestion_pacientes.controller;

import com.SaludPlus.gestion_pacientes.dto.AuthResponse;
import com.SaludPlus.gestion_pacientes.dto.LoginRequest;
import com.SaludPlus.gestion_pacientes.model.User;
import com.SaludPlus.gestion_pacientes.security.JwtService;
import com.SaludPlus.gestion_pacientes.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y generación de tokens JWT")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService; // Necesario para cargar autoridades

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT que incluye sus roles.")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Cargamos el UserDetails completo (con sus roles/authorities)
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Pasamos el objeto userDetails completo para que el token lleve los roles
        String token = jwtService.generateToken(userDetails);
        
        return new AuthResponse(token);
    }
}