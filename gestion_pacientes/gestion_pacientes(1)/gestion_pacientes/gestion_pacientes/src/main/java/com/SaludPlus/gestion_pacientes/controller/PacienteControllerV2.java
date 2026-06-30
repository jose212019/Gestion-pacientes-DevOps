package com.SaludPlus.gestion_pacientes.controller;

import com.SaludPlus.gestion_pacientes.assemblers.PacienteModelAssembler;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/pacientes")
@Tag(name = "1V2. Gestión de Pacientes HATEOAS", description = "Endpoints con HATEOAS para navegación dinámica de la API")
@SecurityRequirement(name = "SaludPlusSecurity")
public class PacienteControllerV2 {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PacienteModelAssembler assembler;

    @Operation(summary = "Obtener catálogo completo", description = "Recupera la lista total de pacientes con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public CollectionModel<EntityModel<Paciente>> listar() {
        List<EntityModel<Paciente>> pacientes = pacienteService.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pacientes,
                linkTo(methodOn(PacienteControllerV2.class).listar()).withSelfRel());
    }

    @Operation(summary = "Buscar por ID", description = "Localiza la ficha con enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese ID")
    })
    @GetMapping("/{id}")
    public EntityModel<Paciente> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Paciente paciente = pacienteService.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe ningún paciente con ID: " + id
                ));
        return assembler.toModel(paciente);
    }

    @Operation(summary = "Buscar por RUN", description = "Localiza un paciente por RUN con enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente localizado"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese RUN")
    })
    @GetMapping("/run/{run}")
    public EntityModel<Paciente> buscarPorRun(
            @Parameter(description = "RUN sin puntos y con guion", example = "12345678-9")
            @PathVariable String run) {
        Paciente paciente = pacienteService.findByRun(run)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No existe ningún paciente con RUN: " + run
                ));
        return assembler.toModel(paciente);
    }

    @Operation(summary = "Registrar Paciente", description = "Crea un nuevo paciente y retorna el recurso con enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "El RUN o correo ya está registrado")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Paciente>> guardar(@Valid @RequestBody Paciente paciente) {
        Paciente nuevo = pacienteService.save(paciente);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(nuevo));
    }

    @Operation(summary = "Modificar Ficha Clínica", description = "Actualiza el paciente y retorna el recurso con enlaces HATEOAS.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "No existe ningún paciente con ese ID"),
        @ApiResponse(responseCode = "409", description = "El RUN o correo ya pertenece a otro paciente")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Paciente>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Paciente paciente) {
        Paciente actualizado = pacienteService.update(id, paciente);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @Operation(summary = "Eliminar Registro", description = "Elimina el paciente del sistema.")
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