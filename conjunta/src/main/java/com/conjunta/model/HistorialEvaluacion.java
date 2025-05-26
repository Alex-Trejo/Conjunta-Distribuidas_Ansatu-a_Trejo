package com.conjunta.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
public class HistorialEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clienteNombre;
    private String tipoCliente;
    private double montoSolicitado;
    private int plazoEnMeses;
    private String nivelRiesgo;
    private boolean aprobado;
    private int puntajeFinal;
    private String mensaje;
    private double tasaInteres;
    private int plazoAprobado;
    private LocalDateTime fechaConsulta;
}
