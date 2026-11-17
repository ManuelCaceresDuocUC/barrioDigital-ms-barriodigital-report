package cl.barriodigital.barriodigital_ms_report.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;

/**
 * QUÉ ES: repositorio Spring Data JPA de las métricas por trámite (clave primaria de tipo String).
 *
 * QUÉ HACE: Spring Data implementa estos métodos solo con leer sus nombres, salvo el que lleva @Query.
 */
@Repository
public interface ProcedureMetricRepository extends JpaRepository<ProcedureMetric, String> {
    // Busca la fila de un trámite por su nombre (vacío si aún no existe).
    Optional<ProcedureMetric> findByActionName(String actionName);
    // Todas las filas, de más a menos solicitudes (ORDER BY TOTAL_REQUESTS DESC).
    List<ProcedureMetric> findAllByOrderByTotalRequestsDesc();

    // Consulta JPQL (usa nombres de clases y campos Java, no de tablas). Suma las tres columnas de todas
    // las filas; COALESCE(..., 0) devuelve 0 en vez de null cuando la tabla está vacía.
    @Query("SELECT COALESCE(SUM(p.totalRequests), 0) AS totalRequests, " +
           "COALESCE(SUM(p.successCount), 0) AS totalSuccess, " +
           "COALESCE(SUM(p.failedCount), 0) AS totalFailed " +
           "FROM ProcedureMetric p")
    KpiSummary getGlobalKpis();

    // "Proyección" de interfaz: Spring llena estos getters con las columnas del SELECT (por su alias)
    // y Jackson los serializa como JSON {"totalRequests":..,"totalSuccess":..,"totalFailed":..}.
    interface KpiSummary {
        Long getTotalRequests();
        Long getTotalSuccess();
        Long getTotalFailed();
    }
}
