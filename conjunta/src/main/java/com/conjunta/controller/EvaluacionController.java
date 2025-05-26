package com.conjunta.controller;

import com.conjunta.dto.EvaluacionRequestDTO;
import com.conjunta.dto.EvaluacionResponseDTO;
import com.conjunta.model.HistorialEvaluacion;
import com.conjunta.service.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaluacionService;

    @PostMapping("/evaluar-riesgo")
    public ResponseEntity<EvaluacionResponseDTO> evaluarRiesgo(@RequestBody EvaluacionRequestDTO request) {
        try {
            EvaluacionResponseDTO response = evaluacionService.evaluarRiesgo(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialEvaluacion>> getHistorial() {
        return ResponseEntity.ok(evaluacionService.getHistorial());
    }
}