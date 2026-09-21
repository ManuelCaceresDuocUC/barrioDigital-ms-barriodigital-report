package cl.barriodigital.barriodigital_ms_report.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ProcedureMetricRepository metricRepository;

    // GET /api/report/kpis
    @GetMapping("/kpis")
    public ResponseEntity<ProcedureMetricRepository.KpiSummary> getKpis() {
        return ResponseEntity.ok(metricRepository.getGlobalKpis());
    }

    // GET /api/report/top-procedures
    @GetMapping("/top-procedures")
    public ResponseEntity<List<ProcedureMetric>> getTopProcedures() {
        return ResponseEntity.ok(metricRepository.findAllByOrderByTotalRequestsDesc());
    }
}