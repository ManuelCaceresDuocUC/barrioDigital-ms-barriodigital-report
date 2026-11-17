package cl.barriodigital.barriodigital_ms_report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import lombok.RequiredArgsConstructor;

/**
 * QUÉ ES: controlador REST de solo lectura con las métricas calculadas.
 *
 * QUÉ HACE: entrega al frontend (vía BFF) los KPIs globales y la tabla por tipo de trámite.
 * Nota de estudio: aquí no hay @PreAuthorize; la restricción a Admin está solo en el frontend (roleGuard)
 * y el BFF exige estar autenticado. Es una mejora pendiente.
 */
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    // Se usa el repositorio directamente porque las consultas son simples (no hay lógica de negocio).
    private final ProcedureMetricRepository metricRepository;

    // GET /api/report/kpis
    // Devuelve {totalRequests, totalSuccess, totalFailed} sumando todas las filas de la tabla.
    @GetMapping("/kpis")
    public ResponseEntity<ProcedureMetricRepository.KpiSummary> getKpis() {
        return ResponseEntity.ok(metricRepository.getGlobalKpis());
    }

    // GET /api/report/top-procedures
    // Lista de trámites ordenada de mayor a menor cantidad de solicitudes.
    @GetMapping("/top-procedures")
    public ResponseEntity<List<ProcedureMetric>> getTopProcedures() {
        return ResponseEntity.ok(metricRepository.findAllByOrderByTotalRequestsDesc());
    }
}
