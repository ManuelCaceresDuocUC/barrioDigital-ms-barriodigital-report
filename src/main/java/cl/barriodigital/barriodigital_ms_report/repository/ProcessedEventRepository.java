package cl.barriodigital.barriodigital_ms_report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.barriodigital.barriodigital_ms_report.entity.ProcessedEvent;

/**
 * QUÉ ES: repositorio de los eventos ya contados.
 *
 * QUÉ HACE: no declara métodos propios: MetricsService usa existsById(eventId) y save(...), que vienen
 * incluidos en JpaRepository.
 */
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
