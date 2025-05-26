package com.conjunta.dto;

import lombok.Data;

import java.util.List;

@Data
public class EvaluacionRequestDTO {
    private String tipoCliente;
    private String nombre;
    private int puntajeCrediticio;
    private List<Deuda> deudasActuales;
    private double montoSolicitado;
    private int plazoEnMeses;
    private Double ingresoMensual; // Para PersonaNatural
    private Integer edad; // Para PersonaNatural
    private Double ingresoAnual; // Para PersonaJuridica
    private Integer antiguedadAnios; // Para PersonaJuridica
    private Integer empleados; // Para PersonaJuridica
}