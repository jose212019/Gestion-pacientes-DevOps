package com.SaludPlus.gestion_pacientes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUN es obligatorio")
    @Pattern(
        regexp = "^\\d{7,8}-[\\dkK]$",
        message = "El RUN debe tener el formato 12345678-9 o 12345678-K"
    )
    @Column(unique = true, length = 15, nullable = false)
    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Column(nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
        regexp = "^[+]?[0-9]{8,15}$",
        message = "El teléfono debe contener solo números y tener entre 8 y 15 dígitos"
    )
    @Column(nullable = false)
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String direccion;

    @NotBlank(message = "La previsión es obligatoria")
    @Column(nullable = false)
    private String prevision;

    @NotBlank(message = "El género es obligatorio")
    @Pattern(
        regexp = "^(Masculino|Femenino|Otro)$",
        message = "El género debe ser: Masculino, Femenino u Otro"
    )
    @Column(nullable = false)
    private String genero;
}