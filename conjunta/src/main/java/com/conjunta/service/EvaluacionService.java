package com.conjunta.service;

import com.conjunta.dto.EvaluacionRequestDTO;
import com.conjunta.dto.EvaluacionResponseDTO;


import com.conjunta.model.*;
import com.conjunta.repository.HistorialEvaluacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {
    @Autowired
    private HistorialEvaluacionRepository historialRepository;

    public EvaluacionResponseDTO evaluarRiesgo(EvaluacionRequestDTO request) {
        // Validaciones básicas
        if (request.getTipoCliente() == null || request.getMontoSolicitado() <= 0 || request.getPlazoEnMeses() <= 0) {
            throw new IllegalArgumentException("Datos inválidos o incompletos");
        }

        Cliente cliente;
        if ("NATURAL".equalsIgnoreCase(request.getTipoCliente())) {
            if (request.getIngresoMensual() == null || request.getEdad() == null) {
                throw new IllegalArgumentException("Faltan datos para Persona Natural");
            }
            cliente = new PersonaNatural();
            ((PersonaNatural) cliente).setIngresoMensual(request.getIngresoMensual());
            ((PersonaNatural) cliente).setEdad(request.getEdad());
        } else if ("JURIDICA".equalsIgnoreCase(request.getTipoCliente())) {
            if (request.getIngresoAnual() == null || request.getAntiguedadAnios() == null || request.getEmpleados() == null) {
                throw new IllegalArgumentException("Faltan datos para Persona Jurídica");
            }
            cliente = new PersonaJuridica();
            ((PersonaJuridica) cliente).setIngresoAnual(request.getIngresoAnual());
            ((PersonaJuridica) cliente).setAntiguedadAnios(request.getAntiguedadAnios());
            ((PersonaJuridica) cliente).setEmpleados(request.getEmpleados());
        } else {
            throw new IllegalArgumentException("Tipo de cliente no válido");
        }

        cliente.setNombre(request.getNombre());
        cliente.setPuntajeCrediticio(request.getPuntajeCrediticio());
        cliente.setDeudasActuales(request.getDeudasActuales());
        cliente.setMontoSolicitado(request.getMontoSolicitado());
        cliente.setPlazoEnMeses(request.getPlazoEnMeses());

        // Cálculo del puntaje
        int puntajeBase = 100;
        int puntajeActual = puntajeBase;

        // Penalización por puntaje crediticio
        if (cliente.getPuntajeCrediticio() < 650) {
            puntajeActual -= 30;
        }

        // Penalización por deudas
        double deudaTotal = cliente.getDeudasActuales().stream().mapToDouble(Deuda::getMonto).sum();
        if ("NATURAL".equalsIgnoreCase(request.getTipoCliente()) && (deudaTotal / cliente.getIngreso() * 100 > 40)) {
            puntajeActual -= 15;
        } else if ("JURIDICA".equalsIgnoreCase(request.getTipoCliente()) && (deudaTotal / cliente.getIngreso() * 100 > 35)) {
            puntajeActual -= 20;
        }

        // Penalización por monto solicitado
        if ("NATURAL".equalsIgnoreCase(request.getTipoCliente()) && (cliente.getMontoSolicitado() / cliente.getIngreso() * 100 > 50)) {
            puntajeActual -= 10;
        } else if ("JURIDICA".equalsIgnoreCase(request.getTipoCliente()) && (cliente.getMontoSolicitado() / cliente.getIngreso() * 100 > 30)) {
            puntajeActual -= 15;
        }

        // Determinar nivel de riesgo
        int finalPuntajeActual = puntajeActual;
        EvaluadorRiesgo evaluador = Arrays.asList(new EvaluadorRiesgoBajo(), new EvaluadorRiesgoMedio(), new EvaluadorRiesgoAlto())
                .stream()
                .filter(e -> {
                    EvaluacionResponseDTO response = e.evaluar(cliente);
                    return finalPuntajeActual >= 80 && "BAJO".equals(response.getNivelRiesgo()) ||
                            (finalPuntajeActual >= 60 && finalPuntajeActual <= 79 && "MEDIO".equals(response.getNivelRiesgo())) ||
                            (finalPuntajeActual < 60 && "ALTO".equals(response.getNivelRiesgo()));
                })
                .findFirst()
                .orElse(new EvaluadorRiesgoAlto());

        EvaluacionResponseDTO response = evaluador.evaluar(cliente);
        response.setPuntajeFinal(puntajeActual);

        // Guardar en historial
        HistorialEvaluacion historial = new HistorialEvaluacion();
        historial.setClienteNombre(cliente.getNombre());
        historial.setTipoCliente(request.getTipoCliente());
        historial.setMontoSolicitado(cliente.getMontoSolicitado());
        historial.setPlazoEnMeses(cliente.getPlazoEnMeses());
        historial.setNivelRiesgo(response.getNivelRiesgo());
        historial.setAprobado(response.isAprobado());
        historial.setPuntajeFinal(response.getPuntajeFinal());
        historial.setMensaje(response.getMensaje());
        historial.setTasaInteres(response.getTasaInteres());
        historial.setPlazoAprobado(response.getPlazoAprobado());
        historial.setFechaConsulta(LocalDateTime.now());
        historialRepository.save(historial);

        return response;
    }

    public List<HistorialEvaluacion> getHistorial() {
        return historialRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }
}
