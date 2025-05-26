package com.conjunta.repository;


import com.conjunta.model.Cliente;
import com.conjunta.model.HistorialEvaluacion;
import com.conjunta.model.PersonaNatural;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialEvaluacionRepository extends JpaRepository<HistorialEvaluacion, Long> {
}

