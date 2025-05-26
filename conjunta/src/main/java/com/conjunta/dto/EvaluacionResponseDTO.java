package com.conjunta.dto;

import lombok.Data;

@Data
public class EvaluacionResponseDTO {
    private String nivelRiesgo;
    private boolean aprobado;
    private int puntajeFinal;
    private String mensaje;
    private double tasaInteres;
    private int plazoAprobado;
}