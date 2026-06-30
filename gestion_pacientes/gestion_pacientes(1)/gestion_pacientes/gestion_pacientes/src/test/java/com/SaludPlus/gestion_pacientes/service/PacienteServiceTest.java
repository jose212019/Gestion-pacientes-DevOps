package com.SaludPlus.gestion_pacientes.service;

import com.SaludPlus.gestion_pacientes.exception.DatosDuplicadosException;
import com.SaludPlus.gestion_pacientes.exception.RecursoNoEncontradoException;
import com.SaludPlus.gestion_pacientes.exception.ValidacionException;
import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PacienteServiceTest {

    @InjectMocks
    private PacienteService pacienteService;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private BackupService backupService;

    private Paciente crearPaciente() {
        Paciente p = new Paciente();
        p.setId(1L);
        p.setRun("15654295-4");
        p.setNombre("Juan");
        p.setApellido("Pérez");
        p.setCorreo("juan@saludplus.cl");
        p.setTelefono("+56912345678");
        p.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        p.setDireccion("Calle 123, Santiago");
        p.setPrevision("Fonasa B");
        p.setGenero("Masculino");
        return p;
    }

    @Test
    void testFindAll() {
        when(pacienteRepository.findAll()).thenReturn(List.of(crearPaciente()));

        List<Paciente> resultado = pacienteService.findAll();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void testFindById() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(crearPaciente()));

        Optional<Paciente> resultado = pacienteService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void testFindByIdNoExiste() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Paciente> resultado = pacienteService.findById(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void testSave() {
        Paciente paciente = crearPaciente();

        when(pacienteRepository.existsByRun(paciente.getRun())).thenReturn(false);
        when(pacienteRepository.existsByCorreo(paciente.getCorreo())).thenReturn(false);
        when(pacienteRepository.save(paciente)).thenReturn(paciente);

        Paciente resultado = pacienteService.save(paciente);

        assertNotNull(resultado);
        assertEquals("15654295-4", resultado.getRun());
    }

    @Test
    void testSaveRunDuplicado() {
        Paciente paciente = crearPaciente();

        when(pacienteRepository.existsByRun(paciente.getRun())).thenReturn(true);

        assertThrows(DatosDuplicadosException.class, () -> pacienteService.save(paciente));
    }

    @Test
    void testSaveCorreoDuplicado() {
        Paciente paciente = crearPaciente();

        when(pacienteRepository.existsByRun(paciente.getRun())).thenReturn(false);
        when(pacienteRepository.existsByCorreo(paciente.getCorreo())).thenReturn(true);

        assertThrows(DatosDuplicadosException.class, () -> pacienteService.save(paciente));
    }

    @Test
    void testSaveRunFormatoInvalido() {
        Paciente paciente = crearPaciente();
        paciente.setRun("run-invalido");

        assertThrows(ValidacionException.class, () -> pacienteService.save(paciente));
    }

    @Test
    void testDeleteNoExiste() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> pacienteService.delete(99L));
    }

    @Test
    void testDeleteExitoso() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pacienteRepository).deleteById(1L);

        assertDoesNotThrow(() -> pacienteService.delete(1L));
        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByRun() {
        Paciente paciente = crearPaciente();
        when(pacienteRepository.findByRun("15654295-4")).thenReturn(Optional.of(paciente));

        Optional<Paciente> resultado = pacienteService.findByRun("15654295-4");

        assertTrue(resultado.isPresent());
        assertEquals("15654295-4", resultado.get().getRun());
    }
}