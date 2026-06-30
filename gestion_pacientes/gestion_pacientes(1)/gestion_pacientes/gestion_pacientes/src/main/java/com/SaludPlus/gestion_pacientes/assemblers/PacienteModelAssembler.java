package com.SaludPlus.gestion_pacientes.assemblers;

import com.SaludPlus.gestion_pacientes.controller.PacienteControllerV2;
import com.SaludPlus.gestion_pacientes.model.Paciente;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PacienteModelAssembler implements RepresentationModelAssembler<Paciente, EntityModel<Paciente>> {

    @Override
    public EntityModel<Paciente> toModel(Paciente paciente) {
        return EntityModel.of(paciente,
            linkTo(methodOn(PacienteControllerV2.class).buscarPorId(paciente.getId(), null)).withSelfRel(),
            linkTo(methodOn(PacienteControllerV2.class).listar()).withRel("pacientes")
        );
    }
}
