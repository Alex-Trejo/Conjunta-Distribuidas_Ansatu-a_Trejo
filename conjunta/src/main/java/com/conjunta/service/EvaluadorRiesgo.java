package com.conjunta.service;

import com.conjunta.dto.EvaluacionResponseDTO;
import com.conjunta.model.Cliente;

public abstract class EvaluadorRiesgo {
    public abstract EvaluacionResponseDTO evaluar(Cliente cliente);
}
