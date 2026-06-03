package com.agrosatmonitor.soap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ALERTA_AGRICOLA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertaAgricola {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_alerta_s")
    @SequenceGenerator(name = "sq_alerta_s", sequenceName = "SQ_ALERTA", allocationSize = 1)
    @Column(name = "ID_ALERTA")
    private Long id;

    @Column(name = "ID_FAZENDA")
    private Long fazendaId;

    @Column(name = "TP_ALERTA")
    private Integer tipoAlertaCodigo;

    @Column(name = "TP_NIVEL_RISCO")
    private Integer nivelRiscoCodigo;

    @Column(name = "DS_ALERTA")
    private String descricao;

    @Column(name = "DT_GERACAO")
    private LocalDateTime dataGeracao;
}
