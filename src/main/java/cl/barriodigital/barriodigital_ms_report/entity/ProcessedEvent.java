package cl.barriodigital.barriodigital_ms_report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Eventos ya contados: Kafka entrega al menos una vez, así que un mismo evento puede llegar repetido.
 *
 * QUÉ ES: tabla PROCESSED_EVENTS, el "libro de registro" de eventos ya contados.
 * CÓMO SE USA: antes de sumar un evento, MetricsService pregunta si su eventId ya está aquí;
 * si está, lo ignora. Así un reenvío de Kafka no infla los KPIs.
 */
@Entity
@Table(name = "PROCESSED_EVENTS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    // El eventId (UUID de hasta 64 caracteres) es la clave primaria: no puede insertarse dos veces.
    @Id
    @Column(name = "EVENT_ID", length = 64)
    private String eventId;

    // Cuándo se contó (para depuración).
    @Column(name = "PROCESSED_AT", nullable = false)
    private LocalDateTime processedAt;
}
