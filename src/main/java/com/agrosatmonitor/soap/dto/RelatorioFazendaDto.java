package com.agrosatmonitor.soap.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RelatorioFazendaDto {
    private Long fazendaId;
    private String nomeFazenda;
    private Double temperaturaMedia;
    private Double umidadeMedia;
    private Double precipitacaoTotal;
    private Double ndviMedio;
    private Long quantidadeAlertas;
    private String periodoInicio;
    private String periodoFim;
}
