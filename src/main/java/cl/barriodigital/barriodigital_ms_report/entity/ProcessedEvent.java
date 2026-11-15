package cl.barriodigital.barriodigital_ms_report.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Eventos ya contados: Kafka entrega al menos una vez, así que un mismo evento puede llegar repetido. */
@Entity
@Table(name = "PROCESSED_EVENTS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    @Id
    @Column(name = "EVENT_ID", length = 64)
    private String eventId;

    @Column(name = "PROCESSED_AT", nullable = false)
    private LocalDateTime processedAt;
}
