package com.agrosatmonitor.soap.endpoint;

import com.agrosatmonitor.soap.dto.RiscoAgricolaDto;
import com.agrosatmonitor.soap.service.RiscoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Endpoint SOAP para análise de risco agrícola.
 * Responde à operação ProcessarRiscoAgricola definida no XSD.
 */
@Endpoint
@RequiredArgsConstructor
@Slf4j
public class RiscoEndpoint {

    private static final String NAMESPACE = "http://agrosatmonitor.com/soap";

    private final RiscoService riscoService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "ProcessarRiscoRequest")
    @ResponsePayload
    public Element processarRisco(@RequestPayload Element request) throws Exception {
        log.info("[SOAP Endpoint] ProcessarRiscoAgricola recebido");

        Long fazendaId = Long.parseLong(
                request.getElementsByTagNameNS(NAMESPACE, "fazendaId").item(0).getTextContent());

        RiscoAgricolaDto dto = riscoService.processarRisco(fazendaId);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "ProcessarRiscoResponse");

        addElement(doc, response, "fazendaId",      String.valueOf(dto.getFazendaId()));
        addElement(doc, response, "nivelRisco",     dto.getNivelRisco());
        addElement(doc, response, "pontuacaoRisco", String.valueOf(dto.getPontuacaoRisco()));
        addElement(doc, response, "motivo",         dto.getMotivo());
        addElement(doc, response, "recomendacao",   dto.getRecomendacao());
        addElement(doc, response, "dataAnalise",    dto.getDataAnalise());

        log.info("[SOAP Endpoint] Risco calculado: {} para fazenda ID={}", dto.getNivelRisco(), fazendaId);
        return response;
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        Element el = doc.createElementNS(NAMESPACE, name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }
}
