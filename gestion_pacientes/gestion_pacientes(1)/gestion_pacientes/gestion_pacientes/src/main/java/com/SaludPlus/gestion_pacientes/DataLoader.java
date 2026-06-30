package com.SaludPlus.gestion_pacientes;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import com.SaludPlus.gestion_pacientes.model.Role;
import com.SaludPlus.gestion_pacientes.model.User;
import com.SaludPlus.gestion_pacientes.repository.PacienteRepository;
import com.SaludPlus.gestion_pacientes.repository.UserRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (pacienteRepository.count() > 0) {
            System.out.println("[DataLoader] La base de datos ya tiene datos, se omite la carga.");
            return;
        }

        Faker faker = new Faker(new Locale("es"));

        // ── Crear usuarios admin y user siempre desde cero
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("[DataLoader] Usuario 'admin' creado.");

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
            System.out.println("[DataLoader] Usuario 'user' creado.");
        }

        // ── Generar 20 pacientes
        String[] previsiones = {
            "Fonasa A", "Fonasa B", "Fonasa C", "Fonasa D",
            "Isapre Cruz Blanca", "Isapre Banmédica"
        };
        String[] generos = {"Masculino", "Femenino", "Otro"};

        for (int i = 1; i <= 20; i++) {
            Paciente paciente = new Paciente();

            int cuerpo = faker.number().numberBetween(5000000, 25000000);
            String dv = String.valueOf(faker.number().numberBetween(0, 9));
            paciente.setRun(cuerpo + "-" + dv);

            paciente.setNombre(faker.name().firstName());
            paciente.setApellido(faker.name().lastName());

            LocalDate fechaNac = faker.date()
                    .birthday(18, 74)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            paciente.setFechaNacimiento(fechaNac);

            paciente.setCorreo("paciente" + i + "@saludplus.cl");
            paciente.setTelefono("+569" + faker.number().numberBetween(10000000, 99999999));
            paciente.setDireccion(faker.address().streetAddress() + ", " + faker.address().city());
            paciente.setPrevision(previsiones[faker.number().numberBetween(0, previsiones.length)]);
            paciente.setGenero(generos[faker.number().numberBetween(0, generos.length)]);

            pacienteRepository.save(paciente);
        }

        System.out.println("[DataLoader] 20 pacientes de prueba generados con DataFaker.");
    }
}