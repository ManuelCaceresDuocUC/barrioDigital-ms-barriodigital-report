package cl.barriodigital.barriodigital_ms_report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QUÉ ES: punto de entrada del microservicio de reportería (ms-report).
 *
 * QUÉ HACE: consume el tópico Kafka "requests.events", mantiene contadores por tipo de trámite en Oracle
 * (tabla PROCEDURE_METRICS) y los expone como KPIs para la pantalla de Reportería.
 */
@SpringBootApplication
public class BarriodigitalMsReportApplication {

	public static void main(String[] args) {
		// Arranca Spring, el servidor web y el consumidor de Kafka.
		SpringApplication.run(BarriodigitalMsReportApplication.class, args);
	}

}
