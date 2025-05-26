package com.conjunta.service;

import com.conjunta.dto.EvaluacionResponseDTO;
import com.conjunta.model.Cliente;

public class EvaluadorRiesgoMedio extends EvaluadorRiesgo {
    @Override
    public EvaluacionResponseDTO evaluar(Cliente cliente) {
        EvaluacionResponseDTO response = new EvaluacionResponseDTO();
        response.setNivelRiesgo("MEDIO");
        response.setAprobado(true);
        response.setPuntajeFinal(75); // Ejemplo
        response.setMensaje("Cliente apto para préstamo con condiciones ajustadas");
        response.setTasaInteres(8.0);
        response.setPlazoAprobado(cliente.getPlazoEnMeses());
        return response;
    }
}
