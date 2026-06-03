package com.agrosatmonitor.soap.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RiscoAgricolaDto {
    private Long fazendaId;
    private String nivelRisco;
    private Double pontuacaoRisco;
    private String motivo;
    private String recomendacao;
    private String dataAnalise;
}
