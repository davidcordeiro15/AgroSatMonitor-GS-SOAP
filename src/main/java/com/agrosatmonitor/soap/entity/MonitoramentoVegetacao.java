package com.agrosatmonitor.soap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MON_VEGETACAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonitoramentoVegetacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_mon_veg_s")
    @SequenceGenerator(name = "sq_mon_veg_s", sequenceName = "SQ_MON_VEG", allocationSize = 1)
    @Column(name = "ID_MON_VEG")
    private Long id;

    @Column(name = "ID_FAZENDA")
    private Long fazendaId;

    @Column(name = "NR_NDVI")
    private Double ndvi;

    @Column(name = "TP_NIVEL_SAUDE")
    private Integer nivelSaudeVegetacaoCodigo;

    @Column(name = "DT_LEITURA")
    private LocalDateTime dataLeitura;
}
