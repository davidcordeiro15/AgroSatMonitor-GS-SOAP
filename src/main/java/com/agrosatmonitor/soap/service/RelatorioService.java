package com.agrosatmonitor.soap.service;

import com.agrosatmonitor.soap.dto.RelatorioFazendaDto;
import com.agrosatmonitor.soap.entity.Fazenda;
import com.agrosatmonitor.soap.exception.ResourceNotFoundException;
import com.agrosatmonitor.soap.repository.AlertaRepository;
import com.agrosatmonitor.soap.repository.ClimaticoRepository;
import com.agrosatmonitor.soap.repository.FazendaRepository;
import com.agrosatmonitor.soap.repository.VegetacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final FazendaRepository fazendaRepository;
    private final ClimaticoRepository climaticoRepository;
    private final VegetacaoRepository vegetacaoRepository;
    private final AlertaRepository alertaRepository;

    public RelatorioFazendaDto consultarRelatorio(Long fazendaId, String dataInicio, String dataFim) {
        log.info("[SOAP] Consultando relatório para fazenda ID={}", fazendaId);

        Fazenda fazenda = fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda", fazendaId));

        Double tempMedia  = climaticoRepository.mediaTemperatura(fazendaId);
        Double umidMedia  = climaticoRepository.mediaUmidade(fazendaId);
        Double precipTotal = climaticoRepository.totalPrecipitacao(fazendaId);
        Double ndviMedio  = vegetacaoRepository.mediaNdvi(fazendaId);
        long qtdAlertas   = alertaRepository.countByFazendaId(fazendaId);

        String inicio = (dataInicio != null && !dataInicio.isBlank()) ? dataInicio : LocalDate.now().minusDays(30).toString();
        String fim    = (dataFim    != null && !dataFim.isBlank())    ? dataFim    : LocalDate.now().toString();

        return RelatorioFazendaDto.builder()
                .fazendaId(fazendaId)
                .nomeFazenda(fazenda.getNome())
                .temperaturaMedia(tempMedia  != null ? Math.round(tempMedia  * 10.0) / 10.0 : 0.0)
                .umidadeMedia(umidMedia  != null ? Math.round(umidMedia  * 10.0) / 10.0 : 0.0)
                .precipitacaoTotal(precipTotal != null ? precipTotal : 0.0)
                .ndviMedio(ndviMedio  != null ? Math.round(ndviMedio  * 10000.0) / 10000.0 : 0.0)
                .quantidadeAlertas(qtdAlertas)
                .periodoInicio(inicio)
                .periodoFim(fim)
                .build();
    }
}
