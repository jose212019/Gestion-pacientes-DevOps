package com.SaludPlus.gestion_pacientes;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class GestionPacientesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionPacientesApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SaludPlus - Gestión de Pacientes")
                .version("1.0")
                .description("Microservicio encargado de la administración y registro de pacientes de la clínica SaludPlus."))
            .addSecurityItem(new SecurityRequirement().addList("SaludPlusSecurity"))
            .components(new Components().addSecuritySchemes("SaludPlusSecurity", new SecurityScheme()
                .name("SaludPlusSecurity")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")));
    }
}