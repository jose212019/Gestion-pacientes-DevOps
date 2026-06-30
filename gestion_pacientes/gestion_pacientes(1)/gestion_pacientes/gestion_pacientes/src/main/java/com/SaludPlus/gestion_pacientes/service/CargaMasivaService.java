package com.SaludPlus.gestion_pacientes.service;

import com.SaludPlus.gestion_pacientes.model.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CargaMasivaService {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void procesarCargaMasiva(List<Paciente> pacientes) {
        int batchSize = 50;
        for (int i = 0; i < pacientes.size(); i++) {
            entityManager.persist(pacientes.get(i));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
}