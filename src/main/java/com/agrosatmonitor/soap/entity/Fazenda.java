package com.agrosatmonitor.soap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_FAZENDA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fazenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_fazenda_s")
    @SequenceGenerator(name = "sq_fazenda_s", sequenceName = "SQ_FAZENDA", allocationSize = 1)
    @Column(name = "ID_FAZENDA")
    private Long id;

    @Column(name = "NM_FAZENDA")
    private String nome;

    @Column(name = "NR_LATITUDE")
    private Double latitude;

    @Column(name = "NR_LONGITUDE")
    private Double longitude;

    @Column(name = "NR_AREA_HECTARES")
    private Double areaHectares;

    @Column(name = "NM_CIDADE")
    private String cidade;

    @Column(name = "SG_ESTADO")
    private String estado;

    @Column(name = "DT_CADASTRO")
    private LocalDateTime dataCadastro;
}
