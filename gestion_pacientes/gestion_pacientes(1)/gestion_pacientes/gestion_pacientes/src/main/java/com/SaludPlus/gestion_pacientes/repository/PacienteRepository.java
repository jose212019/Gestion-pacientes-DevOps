package com.SaludPlus.gestion_pacientes.repository;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRun(String run);

    boolean existsByRun(String run);

    // Necesario para validar correo duplicado en save() y update()
    Optional<Paciente> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}