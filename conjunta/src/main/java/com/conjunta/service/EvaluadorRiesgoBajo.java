package com.conjunta.service;

import com.conjunta.dto.EvaluacionResponseDTO;
import com.conjunta.model.Cliente;

public class EvaluadorRiesgoBajo extends EvaluadorRiesgo {
    @Override
    public EvaluacionResponseDTO evaluar(Cliente cliente) {
        EvaluacionResponseDTO response = new EvaluacionResponseDTO();
        response.setNivelRiesgo("BAJO");
        response.setAprobado(true);
        response.setPuntajeFinal(90); // Ejemplo
        response.setMensaje("Cliente apto para préstamo con condiciones preferenciales");
        response.setTasaInteres(6.5);
        response.setPlazoAprobado(cliente.getPlazoEnMeses());
        return response;
    }
}
