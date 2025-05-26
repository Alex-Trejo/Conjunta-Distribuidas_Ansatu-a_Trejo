package com.conjunta.model;


import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Data
public class PersonaNatural extends Cliente{
    private double ingresoMensual;
    private int edad;

    @Override
    public double getIngreso() {
        return ingresoMensual;
    }

    @Override
    public boolean esAptoParaCredito() {
        return true; // Lógica en Evaluador
    }
}
