package com.conjunta.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
public class PersonaJuridica extends Cliente{

    private double ingresoAnual;
    private int antiguedadAnios;
    private int empleados;

    @Override
    public double getIngreso() {
        return ingresoAnual;
    }

    @Override
    public boolean esAptoParaCredito() {
        return true; // Lógica en Evaluador
    }
}
