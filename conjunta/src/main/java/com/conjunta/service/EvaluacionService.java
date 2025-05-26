package com.conjunta.service;

import com.conjunta.dto.EvaluacionRequestDTO;
import com.conjunta.dto.EvaluacionResponseDTO;


import com.conjunta.model.*;
import com.conjunta.repository.ClienteRepository;
import com.conjunta.repository.HistorialEvaluacionRepository;
import com.conjunta.repository.PersonaJuridicaRepository;
import com.conjunta.repository.PersonaNaturalRepository;
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
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PersonaNaturalRepository personaNaturalRepository;
    @Autowired
    private PersonaJuridicaRepository personaJuridicaRepository;

    public EvaluacionResponseDTO evaluarRiesgo(EvaluacionRequestDTO request) {
        // Validaciones básicas
        if (request.getTipoCliente() == null || request.getMontoSolicitado() <= 0 || request.getPlazoEnMeses() <= 0) {
            throw new IllegalArgumentException("Datos inválidos o incompletos");
        }

        //Validaciones
        if (request.getDeudasActuales() == null || request.getDeudasActuales().isEmpty() || request.getDeudasActuales().stream().anyMatch(d -> d.getMonto() <= 0) ) {
            throw new IllegalArgumentException("Corregir las deudas actuales");
        }

        if (request.getIngresoMensual() == null || request.getIngresoMensual() <= 0) {
            throw new IllegalArgumentException("Corregir el ingreso mensual");
        }

        if (request.getEdad() == null || request.getEdad() <= 0) {
            throw new IllegalArgumentException("Corregir la edad");
        }




        Cliente cliente;
        if ("NATURAL".equalsIgnoreCase(request.getTipoCliente())) {
            if (request.getIngresoMensual() == null || request.getEdad() == null) {
                throw new IllegalArgumentException("Faltan datos para Persona Natural");
            }
            PersonaNatural personaNatural = new PersonaNatural();
            personaNatural.setNombre(request.getNombre());
            personaNatural.setPuntajeCrediticio(request.getPuntajeCrediticio());
            personaNatural.setDeudasActuales(request.getDeudasActuales());
            personaNatural.setMontoSolicitado(request.getMontoSolicitado());
            personaNatural.setPlazoEnMeses(request.getPlazoEnMeses());
            personaNatural.setIngresoMensual(request.getIngresoMensual());
            personaNatural.setEdad(request.getEdad());
            cliente = personaNaturalRepository.save(personaNatural); // Guardar en la tabla PersonaNatural
        } else if ("JURIDICA".equalsIgnoreCase(request.getTipoCliente())) {
            if (request.getIngresoAnual() == null || request.getAntiguedadAnios() == null || request.getEmpleados() == null) {
                throw new IllegalArgumentException("Faltan datos para Persona Jurídica");
            }
            PersonaJuridica personaJuridica = new PersonaJuridica();
            personaJuridica.setNombre(request.getNombre());
            personaJuridica.setPuntajeCrediticio(request.getPuntajeCrediticio());
            personaJuridica.setDeudasActuales(request.getDeudasActuales());
            personaJuridica.setMontoSolicitado(request.getMontoSolicitado());
            personaJuridica.setPlazoEnMeses(request.getPlazoEnMeses());
            personaJuridica.setIngresoAnual(request.getIngresoAnual());
            personaJuridica.setAntiguedadAnios(request.getAntiguedadAnios());
            personaJuridica.setEmpleados(request.getEmpleados());
            cliente = personaJuridicaRepository.save(personaJuridica); // Guardar en la tabla PersonaJuridica
        } else {
            throw new IllegalArgumentException("Tipo de cliente no válido");
        }

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

        // Guardar en historial con referencia al cliente
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
