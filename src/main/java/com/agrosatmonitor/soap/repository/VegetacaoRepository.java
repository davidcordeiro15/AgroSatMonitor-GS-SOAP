package com.agrosatmonitor.soap.repository;

import com.agrosatmonitor.soap.entity.MonitoramentoVegetacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VegetacaoRepository extends JpaRepository<MonitoramentoVegetacao, Long> {

    @Query("SELECT AVG(m.ndvi) FROM MonitoramentoVegetacao m WHERE m.fazendaId = :fid")
    Double mediaNdvi(@Param("fid") Long fazendaId);

    @Query("SELECT m FROM MonitoramentoVegetacao m WHERE m.fazendaId = :fid ORDER BY m.dataLeitura DESC")
    java.util.List<MonitoramentoVegetacao> findByFazendaIdOrderByDataLeituraDesc(@Param("fid") Long fazendaId);
}
