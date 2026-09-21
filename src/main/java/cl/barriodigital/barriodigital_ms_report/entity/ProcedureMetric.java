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

@Entity
@Table(name = "PROCEDURE_METRICS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureMetric {

    @Id
    @Column(name = "ACTION_NAME")
    private String actionName;

    @Column(name = "TOTAL_REQUESTS", nullable = false)
    private Long totalRequests;

    @Column(name = "SUCCESS_COUNT", nullable = false)
    private Long successCount;

    @Column(name = "FAILED_COUNT", nullable = false)
    private Long failedCount;
}