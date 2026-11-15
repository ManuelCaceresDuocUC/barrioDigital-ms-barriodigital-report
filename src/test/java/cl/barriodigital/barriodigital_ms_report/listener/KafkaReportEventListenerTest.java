package cl.barriodigital.barriodigital_ms_report.listener;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cl.barriodigital.barriodigital_ms_report.dto.RequestEvent;
import cl.barriodigital.barriodigital_ms_report.service.MetricsService;

class KafkaReportEventListenerTest {

    private final MetricsService metrics = mock(MetricsService.class);
    private final KafkaReportEventListener listener = new KafkaReportEventListener(metrics);

    @Test
    void leeElEventoQueEnviaRequestsIgnorandoCamposNuevos() {
        listener.consumeRequestEvent("{\"eventId\":\"e1\",\"eventType\":\"REQUEST_CREATED\",\"requestId\":7,"
                + "\"procedureType\":\"Poda\",\"previousStatus\":null,\"status\":\"INGRESADO\",\"campoNuevo\":1}");

        ArgumentCaptor<RequestEvent> captor = ArgumentCaptor.forClass(RequestEvent.class);
        verify(metrics).apply(captor.capture());
        RequestEvent event = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(event.getEventId()).isEqualTo("e1");
        org.assertj.core.api.Assertions.assertThat(event.getRequestId()).isEqualTo(7L);
        org.assertj.core.api.Assertions.assertThat(event.getProcedureType()).isEqualTo("Poda");
    }

    @Test
    void unMensajeQueNoEsJsonSeDescartaSinLanzarExcepcion() {
        assertThatCode(() -> listener.consumeRequestEvent("esto no es json")).doesNotThrowAnyException();

        verify(metrics, never()).apply(any());
    }

    @Test
    void unFalloDeBaseDeDatosSePropagaParaQueSeReintente() {
        when(metrics.apply(any())).thenThrow(new IllegalStateException("Oracle caído"));

        assertThatThrownBy(() -> listener.consumeRequestEvent("{\"eventId\":\"e1\",\"eventType\":\"REQUEST_CREATED\"}"))
                .isInstanceOf(IllegalStateException.class);
    }
}
