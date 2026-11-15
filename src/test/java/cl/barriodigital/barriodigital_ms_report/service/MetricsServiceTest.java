package cl.barriodigital.barriodigital_ms_report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cl.barriodigital.barriodigital_ms_report.dto.RequestEvent;
import cl.barriodigital.barriodigital_ms_report.entity.ProcedureMetric;
import cl.barriodigital.barriodigital_ms_report.entity.ProcessedEvent;
import cl.barriodigital.barriodigital_ms_report.repository.ProcedureMetricRepository;
import cl.barriodigital.barriodigital_ms_report.repository.ProcessedEventRepository;

class MetricsServiceTest {

    private ProcedureMetricRepository metrics;
    private ProcessedEventRepository processed;
    private MetricsService service;

    @BeforeEach
    void setUp() {
        metrics = mock(ProcedureMetricRepository.class);
        processed = mock(ProcessedEventRepository.class);
        service = new MetricsService(metrics, processed);
        when(metrics.findByActionName(anyString())).thenReturn(Optional.empty());
    }

    private static RequestEvent event(String id, String type, String procedure, String status) {
        RequestEvent event = new RequestEvent();
        event.setEventId(id);
        event.setEventType(type);
        event.setProcedureType(procedure);
        event.setStatus(status);
        return event;
    }

    private ProcedureMetric savedMetric() {
        ArgumentCaptor<ProcedureMetric> captor = ArgumentCaptor.forClass(ProcedureMetric.class);
        verify(metrics).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void unTramiteIngresadoSumaAlTotalDeSuTipo() {
        assertThat(service.apply(event("e1", RequestEvent.CREATED, "Poda de árbol", "INGRESADO"))).isTrue();

        ProcedureMetric metric = savedMetric();
        assertThat(metric.getActionName()).isEqualTo("Poda de árbol");
        assertThat(metric.getTotalRequests()).isEqualTo(1);
        assertThat(metric.getSuccessCount()).isZero();
        verify(processed).save(any(ProcessedEvent.class));
    }

    @Test
    void resueltoSumaExitoYRechazadoSumaFallido() {
        ProcedureMetric existing = ProcedureMetric.builder()
                .actionName("Poda").totalRequests(5L).successCount(2L).failedCount(1L).build();
        when(metrics.findByActionName("Poda")).thenReturn(Optional.of(existing));

        service.apply(event("e1", RequestEvent.STATUS_CHANGED, "Poda", "RESUELTO"));
        service.apply(event("e2", RequestEvent.STATUS_CHANGED, "Poda", "RECHAZADO"));

        assertThat(existing.getSuccessCount()).isEqualTo(3);
        assertThat(existing.getFailedCount()).isEqualTo(2);
        assertThat(existing.getTotalRequests()).isEqualTo(5);
    }

    @Test
    void losEstadosIntermediosNoCambianLasMetricasPeroSiQuedanRegistrados() {
        assertThat(service.apply(event("e1", RequestEvent.STATUS_CHANGED, "Poda", "EN_TERRENO"))).isTrue();

        verify(metrics, never()).save(any());
        verify(processed).save(any(ProcessedEvent.class));
    }

    @Test
    void unEventoRepetidoNoSeCuentaDosVeces() {
        when(processed.existsById("e1")).thenReturn(true);

        assertThat(service.apply(event("e1", RequestEvent.CREATED, "Poda", "INGRESADO"))).isFalse();

        verify(metrics, never()).save(any());
        verify(processed, never()).save(any());
    }

    @Test
    void unEventoSinTipoDeTramiteSeCuentaComoSinTipo() {
        service.apply(event("e1", RequestEvent.CREATED, null, "INGRESADO"));

        assertThat(savedMetric().getActionName()).isEqualTo("Sin tipo");
    }

    @Test
    void unEventoIncompletoSeDescarta() {
        assertThat(service.apply(event(null, RequestEvent.CREATED, "Poda", "INGRESADO"))).isFalse();
        assertThat(service.apply(event("e1", " ", "Poda", "INGRESADO"))).isFalse();
        assertThat(service.apply(null)).isFalse();

        verify(metrics, never()).save(any());
    }

    @Test
    void unTipoDeEventoDesconocidoNoRompeNiCambiaMetricas() {
        assertThat(service.apply(event("e1", "OTRO", "Poda", "INGRESADO"))).isTrue();

        verify(metrics, never()).save(any());
    }
}
