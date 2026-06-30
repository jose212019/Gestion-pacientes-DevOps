package com.SaludPlus.gestion_pacientes.controller;

import com.SaludPlus.gestion_pacientes.exception.RecursoNoEncontradoException;
import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "1. Gestión de Pacientes", description = "Endpoints para el mantenimiento de fichas clínicas y carga masiva de datos")
@SecurityRequirement(name = "SaludPlusSecurity")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(summary = "Obtener catálogo completo", description = "Recupera la lista total de pacientes registrados.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public List<Paciente> listar() {
        return pacienteService.findAll();
    }

    @Operation(summary = "Buscar por ID", description = "Localiza la ficha mediante su identificador numérico interno.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        Paciente paciente = pacienteService.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe ningún paciente con ID: " + id
                ));
        return ResponseEntity.ok(paciente);
    }

    @Operation(summary = "Buscar por RUN", description = "Permite localizar un paciente usando su RUN. Método preferido para integración con Atenciones.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente localizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con el RUN proporcionado")
    })
    @GetMapping("/run/{run}")
    public ResponseEntity<Paciente> buscarPorRun(
            @Parameter(description = "RUN sin puntos y con guion", example = "12345678-9")
            @PathVariable String run) {
        Paciente paciente = pacienteService.findByRun(run)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe ningún paciente con RUN: " + run
                ));
        return ResponseEntity.ok(paciente);
    }

    @Operation(summary = "Registrar Paciente", description = "Crea un nuevo registro. Valida formato de RUN, dígito verificador, duplicados y campos obligatorios.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o formato de RUN incorrecto"),
        @ApiResponse(responseCode = "409", description = "El RUN o correo ya está registrado")
    })
    @PostMapping
    public ResponseEntity<Paciente> guardar(@Valid @RequestBody Paciente paciente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.save(paciente));
    }

    @Operation(summary = "Modificar Ficha Clínica", description = "Actualiza la información del paciente. Valida duplicados si cambia RUN o correo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese ID"),
        @ApiResponse(responseCode = "409", description = "El RUN o correo ya pertenece a otro paciente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.update(id, paciente));
    }

    @Operation(summary = "Importación Masiva (Batch)", description = "Procesa múltiples registros optimizando el rendimiento. Aborta si cualquier RUN o correo ya existe.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Carga masiva exitosa"),
        @ApiResponse(responseCode = "400", description = "Algún registro tiene datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Algún RUN o correo ya está registrado")
    })
    @PostMapping("/batch")
    public ResponseEntity<List<Paciente>> cargaMasiva(@Valid @RequestBody List<Paciente> pacientes) {
        return ResponseEntity.ok(pacienteService.saveAll(pacientes));
    }

    @Operation(summary = "Eliminar Registro", description = "Remueve físicamente el registro del sistema.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}