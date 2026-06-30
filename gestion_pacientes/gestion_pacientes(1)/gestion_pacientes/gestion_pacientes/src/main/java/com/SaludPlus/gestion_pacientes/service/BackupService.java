package com.SaludPlus.gestion_pacientes.service;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.File;

@Service
public class BackupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void inicializar() {
        createBackup();
    }

    public void realizarRespaldo(Paciente paciente) {
        insertarEnEspejo(paciente);
        createBackup();
    }

    public void insertarEnEspejo(Paciente paciente) {
        try {
            String sql = """
                INSERT INTO respaldo_pacientes 
                (id_original, rut, nombre, apellido, fecha_nacimiento, correo, telefono, direccion, prevision, genero) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            jdbcTemplate.update(sql,
                paciente.getId(),
                paciente.getRun(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getFechaNacimiento(),
                paciente.getCorreo(),
                paciente.getTelefono(),
                paciente.getDireccion(),
                paciente.getPrevision(),
                paciente.getGenero()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void createBackup() {
        String dumpPath = "C:/laragon/bin/mysql/mysql-8.4.3-winx64/bin/mysqldump.exe";
        String savePath = "C:/backups/backup_pacientes.sql";

        try {
            File directory = new File("C:/backups");
            if (!directory.exists()) directory.mkdirs();

            ProcessBuilder pb = new ProcessBuilder(
                dumpPath,
                "-u", "root",
                "db_paciente_service",
                "--result-file=" + savePath
            );

            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}