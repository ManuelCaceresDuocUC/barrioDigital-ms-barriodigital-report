package cl.barriodigital.barriodigital_ms_report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;

@Repository
public interface ProcedureMetricRepository extends JpaRepository<ProcedureMetric, String> {
    Optional<ProcedureMetric> findByActionName(String actionName);
    List<ProcedureMetric> findAllByOrderByTotalRequestsDesc();

    @Query("SELECT COALESCE(SUM(p.totalRequests), 0) AS totalRequests, " +
           "COALESCE(SUM(p.successCount), 0) AS totalSuccess, " +
           "COALESCE(SUM(p.failedCount), 0) AS totalFailed " +
           "FROM ProcedureMetric p")
    KpiSummary getGlobalKpis();

    interface KpiSummary {
        Long getTotalRequests();
        Long getTotalSuccess();
        Long getTotalFailed();
    }
}