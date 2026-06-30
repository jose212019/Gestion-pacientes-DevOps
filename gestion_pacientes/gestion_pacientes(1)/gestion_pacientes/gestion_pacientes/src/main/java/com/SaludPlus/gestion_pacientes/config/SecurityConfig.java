package com.SaludPlus.gestion_pacientes.config;

import com.SaludPlus.gestion_pacientes.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitado para permitir peticiones POST (Login/Registro)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas públicas de Documentación según personalización de la guía
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/saludplus-pacientes-docs.html", // Ruta personalizada del Micro A
                    "/webjars/**"
                ).permitAll()
                
                // 2. Ruta pública de Autenticación (Indispensable para obtener el token)
                .requestMatchers("/auth/**").permitAll()
                
                // 3. Rutas protegidas que requieren Token con Roles
                .requestMatchers("/api/pacientes/**").authenticated()
                
                // 4. Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            );

        // Insertamos el filtro JWT antes del filtro por defecto de Spring Security
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}