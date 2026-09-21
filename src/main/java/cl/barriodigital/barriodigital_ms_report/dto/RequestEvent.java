package cl.barriodigital.barriodigital_ms_report.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestEvent {
    private Long requestId; // O String, según el tipo que uses
    private String action;
    private String status;
}