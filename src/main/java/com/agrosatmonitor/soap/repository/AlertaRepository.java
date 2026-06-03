package com.agrosatmonitor.soap.repository;

import com.agrosatmonitor.soap.entity.AlertaAgricola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<AlertaAgricola, Long> {
    long countByFazendaId(Long fazendaId);
    List<AlertaAgricola> findByFazendaIdOrderByDataGeracaoDesc(Long fazendaId);
}
