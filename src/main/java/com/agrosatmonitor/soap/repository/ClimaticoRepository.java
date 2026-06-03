package com.agrosatmonitor.soap.repository;

import com.agrosatmonitor.soap.entity.MonitoramentoClimatico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimaticoRepository extends JpaRepository<MonitoramentoClimatico, Long> {

    @Query("SELECT AVG(m.temperatura) FROM MonitoramentoClimatico m WHERE m.fazendaId = :fid")
    Double mediaTemperatura(@Param("fid") Long fazendaId);

    @Query("SELECT AVG(m.umidade) FROM MonitoramentoClimatico m WHERE m.fazendaId = :fid")
    Double mediaUmidade(@Param("fid") Long fazendaId);

    @Query("SELECT COALESCE(SUM(m.precipitacao), 0) FROM MonitoramentoClimatico m WHERE m.fazendaId = :fid")
    Double totalPrecipitacao(@Param("fid") Long fazendaId);
}
