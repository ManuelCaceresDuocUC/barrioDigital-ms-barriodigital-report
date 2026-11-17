package cl.barriodigital.barriodigital_ms_report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * QUÉ ES: configuración de Spring para el manejo de errores del consumidor de Kafka.
 *
 * QUÉ HACE: Spring Kafka detecta este bean y lo aplica automáticamente a todos los @KafkaListener.
 */
@Configuration
public class KafkaConsumerConfig {

    /** Reintenta 3 veces (1 s entre intentos) un mensaje que falla; luego lo registra y sigue con el siguiente. */
    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        // FixedBackOff(espera en ms, reintentos): 1 segundo entre cada uno de los 3 reintentos.
        // Después del último fallo el mensaje se descarta con log para no bloquear la partición.
        return new DefaultErrorHandler(new FixedBackOff(1_000L, 3L));
    }
}
