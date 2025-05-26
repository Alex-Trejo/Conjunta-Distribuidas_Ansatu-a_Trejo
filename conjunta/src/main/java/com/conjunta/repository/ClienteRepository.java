package com.conjunta.repository;

import com.conjunta.model.Cliente;
import com.conjunta.model.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}

