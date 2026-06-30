package com.SaludPlus.gestion_pacientes.controller;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/db")
@ConditionalOnBean(Flyway.class)
public class DatabaseAdminController {
    private final Flyway flyway;

    public DatabaseAdminController(Flyway flyway) {
        this.flyway = flyway;
    }

    @PostMapping("/repair")
    public String repair() {
        flyway.repair();
        return "Flyway reparado exitosamente.";
    }
}