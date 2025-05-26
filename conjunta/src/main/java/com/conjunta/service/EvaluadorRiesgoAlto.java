package com.conjunta.service;

import com.conjunta.dto.EvaluacionResponseDTO;
import com.conjunta.model.Cliente;

public class EvaluadorRiesgoAlto extends EvaluadorRiesgo {
    @Override
    public EvaluacionResponseDTO evaluar(Cliente cliente) {
        EvaluacionResponseDTO response = new EvaluacionResponseDTO();
        response.setNivelRiesgo("ALTO");
        response.setAprobado(false);
        response.setPuntajeFinal(35); // Ejemplo
        response.setMensaje("Cliente no apto para préstamo");
        response.setTasaInteres(0);
        response.setPlazoAprobado(0);
        return response;
    }
}
