package com.conjunta.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int puntajeCrediticio;
    private double montoSolicitado;
    private int plazoEnMeses;
    @ElementCollection
    private List<Deuda> deudasActuales;

    public abstract double getIngreso();
    public abstract boolean esAptoParaCredito();


}
