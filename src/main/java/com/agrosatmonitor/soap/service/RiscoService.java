package com.agrosatmonitor.soap.service;

import com.agrosatmonitor.soap.dto.RiscoAgricolaDto;
import com.agrosatmonitor.soap.entity.AlertaAgricola;
import com.agrosatmonitor.soap.entity.MonitoramentoClimatico;
import com.agrosatmonitor.soap.entity.MonitoramentoVegetacao;
import com.agrosatmonitor.soap.exception.ResourceNotFoundException;
import com.agrosatmonitor.soap.repository.AlertaRepository;
import com.agrosatmonitor.soap.repository.ClimaticoRepository;
import com.agrosatmonitor.soap.repository.FazendaRepository;
import com.agrosatmonitor.soap.repository.VegetacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiscoService {

    private final FazendaRepository fazendaRepository;
    private final ClimaticoRepository climaticoRepository;
    private final VegetacaoRepository vegetacaoRepository;
    private final AlertaRepository alertaRepository;

    /**
     * Processa e calcula o risco agrícola de uma fazenda.
     * Aplica POLIMORFISMO: os diferentes indicadores contribuem
     * para a pontuação de risco de forma independente e combinada.
     */
    public RiscoAgricolaDto processarRisco(Long fazendaId) {
        log.info("[SOAP] Processando risco para fazenda ID={}", fazendaId);

        fazendaRepository.findById(fazendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda", fazendaId));

        double pontuacao = 0.0;
        StringBuilder motivo = new StringBuilder();

        // ── Risco climático ───────────────────────────────────────────────────
        Double tempMedia = climaticoRepository.mediaTemperatura(fazendaId);
        Double umidMedia = climaticoRepository.mediaUmidade(fazendaId);

        if (tempMedia != null) {
            if (tempMedia > 38) {
                pontuacao += 30;
                motivo.append("Temperatura média alta (").append(String.format("%.1f", tempMedia)).append("°C). ");
            } else if (tempMedia > 32) {
                pontuacao += 15;
                motivo.append("Temperatura acima da média (").append(String.format("%.1f", tempMedia)).append("°C). ");
            }
        }

        if (umidMedia != null && umidMedia < 30) {
            pontuacao += 25;
            motivo.append("Umidade baixa (").append(String.format("%.0f", umidMedia)).append("%). ");
        }

        // ── Risco de vegetação ────────────────────────────────────────────────
        Double ndviMedio = vegetacaoRepository.mediaNdvi(fazendaId);
        if (ndviMedio != null) {
            if (ndviMedio < 0.1) {
                pontuacao += 35;
                motivo.append("NDVI crítico (").append(String.format("%.4f", ndviMedio)).append("). ");
            } else if (ndviMedio < 0.25) {
                pontuacao += 20;
                motivo.append("NDVI baixo (").append(String.format("%.4f", ndviMedio)).append("). ");
            }
        }

        // ── Risco por alertas ─────────────────────────────────────────────────
        long qtdAlertas = alertaRepository.countByFazendaId(fazendaId);
        if (qtdAlertas > 5) {
            pontuacao += Math.min(qtdAlertas * 2, 20);
            motivo.append(qtdAlertas).append(" alertas registrados. ");
        }

        // ── Classificação final ───────────────────────────────────────────────
        String nivel;
        String recomendacao;

        if (pontuacao >= 70) {
            nivel = "CRITICO";
            recomendacao = "Intervenção imediata necessária. Contate agrônomo. Verifique irrigação e condições do solo.";
        } else if (pontuacao >= 45) {
            nivel = "ALTO";
            recomendacao = "Monitorar diariamente. Revisar sistema de irrigação e aplicação de defensivos.";
        } else if (pontuacao >= 20) {
            nivel = "MEDIO";
            recomendacao = "Monitoramento regular. Acompanhar previsão climática e índices de vegetação.";
        } else {
            nivel = "BAIXO";
            recomendacao = "Condições favoráveis. Manter monitoramento periódico e boas práticas agrícolas.";
        }

        if (motivo.isEmpty()) motivo.append("Sem indicadores críticos identificados.");

        return RiscoAgricolaDto.builder()
                .fazendaId(fazendaId)
                .nivelRisco(nivel)
                .pontuacaoRisco(Math.min(pontuacao, 100))
                .motivo(motivo.toString().trim())
                .recomendacao(recomendacao)
                .dataAnalise(LocalDateTime.now().toString())
                .build();
    }
}
