package cl.barriodigital.barriodigital_ms_report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.barriodigital.barriodigital_ms_report.entity.ProcessedEvent;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
