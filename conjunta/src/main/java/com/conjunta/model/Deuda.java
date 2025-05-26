package com.conjunta.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Data
public class Deuda implements Serializable {
    private double monto;
    private int plazoMeses;
}
