package com.agrosatmonitor.soap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MON_CLIMATICO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonitoramentoClimatico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_mon_cli_s")
    @SequenceGenerator(name = "sq_mon_cli_s", sequenceName = "SQ_MON_CLI", allocationSize = 1)
    @Column(name = "ID_MON_CLI")
    private Long id;

    @Column(name = "ID_FAZENDA")
    private Long fazendaId;

    @Column(name = "NR_TEMPERATURA")
    private Double temperatura;

    @Column(name = "NR_UMIDADE")
    private Double umidade;

    @Column(name = "NR_PRECIPITACAO")
    private Double precipitacao;

    @Column(name = "NR_VEL_VENTO")
    private Double velocidadeVento;

    @Column(name = "DT_LEITURA")
    private LocalDateTime dataLeitura;

    @Column(name = "DT_CRIACAO")
    private LocalDateTime dataCriacao;
}
