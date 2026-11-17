package cl.barriodigital.barriodigital_ms_report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * QUÉ ES: entidad JPA; una fila de PROCEDURE_METRICS por cada tipo de trámite.
 *
 * QUÉ GUARDA: tres contadores que MetricsService incrementa con cada evento:
 * ingresadas, resueltas (éxito) y rechazadas (fallidas).
 */
@Entity
@Table(name = "PROCEDURE_METRICS")
// Lombok: getters, setters, constructor vacío (lo exige JPA), constructor completo y Builder.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureMetric {

    // La clave primaria es el nombre del trámite: no hay secuencia, el nombre identifica la fila.
    @Id
    @Column(name = "ACTION_NAME")
    private String actionName;

    // Trámites ingresados (eventos REQUEST_CREATED).
    @Column(name = "TOTAL_REQUESTS", nullable = false)
    private Long totalRequests;

    // Trámites que llegaron a RESUELTO.
    @Column(name = "SUCCESS_COUNT", nullable = false)
    private Long successCount;

    // Trámites que terminaron en RECHAZADO.
    @Column(name = "FAILED_COUNT", nullable = false)
    private Long failedCount;
}
