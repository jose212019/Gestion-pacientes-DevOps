package com.SaludPlus.gestion_pacientes.service;

import com.SaludPlus.gestion_pacientes.exception.DatosDuplicadosException;
import com.SaludPlus.gestion_pacientes.exception.RecursoNoEncontradoException;
import com.SaludPlus.gestion_pacientes.exception.ValidacionException;
import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private BackupService backupService;

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }

    public Optional<Paciente> findByRun(String run) {
        return pacienteRepository.findByRun(run);
    }

    public Paciente save(Paciente paciente) {
        validarRun(paciente.getRun());

        if (pacienteRepository.existsByRun(paciente.getRun())) {
            throw new DatosDuplicadosException(
                "El RUN " + paciente.getRun() + " ya está registrado en SaludPlus."
            );
        }

        if (pacienteRepository.existsByCorreo(paciente.getCorreo())) {
            throw new DatosDuplicadosException(
                "El correo " + paciente.getCorreo() + " ya está registrado en SaludPlus."
            );
        }

        Paciente guardado = pacienteRepository.save(paciente);
        backupService.realizarRespaldo(guardado);
        return guardado;
    }

    public Paciente update(Long id, Paciente datosNuevos) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException(
                "No se encontró ningún paciente con ID: " + id
            ));

        if (!paciente.getRun().equals(datosNuevos.getRun())) {
            validarRun(datosNuevos.getRun());
            pacienteRepository.findByRun(datosNuevos.getRun()).ifPresent(otro -> {
                if (!otro.getId().equals(id)) {
                    throw new DatosDuplicadosException(
                        "El RUN " + datosNuevos.getRun() + " ya pertenece a otro paciente."
                    );
                }
            });
        }

        if (!paciente.getCorreo().equals(datosNuevos.getCorreo())) {
            pacienteRepository.findByCorreo(datosNuevos.getCorreo()).ifPresent(otro -> {
                if (!otro.getId().equals(id)) {
                    throw new DatosDuplicadosException(
                        "El correo " + datosNuevos.getCorreo() + " ya pertenece a otro paciente."
                    );
                }
            });
        }

        paciente.setRun(datosNuevos.getRun());
        paciente.setNombre(datosNuevos.getNombre());
        paciente.setApellido(datosNuevos.getApellido());
        paciente.setFechaNacimiento(datosNuevos.getFechaNacimiento());
        paciente.setCorreo(datosNuevos.getCorreo());
        paciente.setTelefono(datosNuevos.getTelefono());
        paciente.setDireccion(datosNuevos.getDireccion());
        paciente.setPrevision(datosNuevos.getPrevision());
        paciente.setGenero(datosNuevos.getGenero());

        Paciente actualizado = pacienteRepository.save(paciente);
        backupService.realizarRespaldo(actualizado);
        return actualizado;
    }

    public List<Paciente> saveAll(List<Paciente> pacientes) {
        pacientes.forEach(p -> {
            validarRun(p.getRun());
            if (pacienteRepository.existsByRun(p.getRun())) {
                throw new DatosDuplicadosException(
                    "Carga masiva abortada: el RUN " + p.getRun() + " ya está registrado."
                );
            }
            if (pacienteRepository.existsByCorreo(p.getCorreo())) {
                throw new DatosDuplicadosException(
                    "Carga masiva abortada: el correo " + p.getCorreo() + " ya está registrado."
                );
            }
        });

        List<Paciente> guardados = pacienteRepository.saveAll(pacientes);
        guardados.forEach(backupService::insertarEnEspejo);
        backupService.createBackup();
        return guardados;
    }

    public void delete(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                "No se puede eliminar: no existe ningún paciente con ID: " + id
            );
        }
        pacienteRepository.deleteById(id);
    }

    // ── Validaciones de RUN ──────────────────────────────────────────────────

    private void validarRun(String run) {
        if (run == null || run.isBlank()) {
            throw new ValidacionException("El RUN no puede estar vacío.");
        }
        if (!run.matches("^\\d{7,8}-[\\dkK]$")) {
            throw new ValidacionException(
                "Formato de RUN inválido: '" + run + "'. Use el formato 12345678-9 o 12345678-K."
            );
        }
        if (!verificarDigitoVerificador(run)) {
            throw new ValidacionException(
                "El dígito verificador del RUN '" + run + "' es incorrecto."
            );
        }
    }

    
    private boolean verificarDigitoVerificador(String run) {
        try {
            String[] partes = run.split("-");
            String dvIngresado = partes[1].toUpperCase();

            // Invertir dígitos de derecha a izquierda
            char[] digitos = new StringBuilder(partes[0]).reverse().toString().toCharArray();

            int suma = 0;
            int[] serie = {2, 3, 4, 5, 6, 7};

            for (int i = 0; i < digitos.length; i++) {
                suma += Character.getNumericValue(digitos[i]) * serie[i % 6];
            }

            int resto = 11 - (suma % 11);
            String dvCalculado = resto == 11 ? "0" : resto == 10 ? "K" : String.valueOf(resto);

            return dvCalculado.equals(dvIngresado);
        } catch (Exception e) {
            return false;
        }
    }
}