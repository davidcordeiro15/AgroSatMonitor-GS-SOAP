package com.agrosatmonitor.soap.endpoint;

import com.agrosatmonitor.soap.dto.RelatorioFazendaDto;
import com.agrosatmonitor.soap.service.RelatorioService;
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
 * Endpoint SOAP para relatórios de fazendas.
 * Opera sobre o namespace http://agrosatmonitor.com/soap
 * e responde à operação ConsultarRelatorioFazenda definida no XSD.
 */
@Endpoint
@RequiredArgsConstructor
@Slf4j
public class RelatorioEndpoint {

    private static final String NAMESPACE = "http://agrosatmonitor.com/soap";

    private final RelatorioService relatorioService;

    @PayloadRoot(namespace = NAMESPACE, localPart = "ConsultarRelatorioRequest")
    @ResponsePayload
    public Element consultarRelatorio(@RequestPayload Element request) throws Exception {
        log.info("[SOAP Endpoint] ConsultarRelatorioFazenda recebido");

        Long fazendaId = Long.parseLong(
                request.getElementsByTagNameNS(NAMESPACE, "fazendaId").item(0).getTextContent());

        String dataInicio = getOptionalText(request, "dataInicio");
        String dataFim    = getOptionalText(request, "dataFim");

        RelatorioFazendaDto dto = relatorioService.consultarRelatorio(fazendaId, dataInicio, dataFim);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "ConsultarRelatorioResponse");

        addElement(doc, response, "fazendaId",        String.valueOf(dto.getFazendaId()));
        addElement(doc, response, "nomeFazenda",       dto.getNomeFazenda());
        addElement(doc, response, "temperaturaMedia",  String.valueOf(dto.getTemperaturaMedia()));
        addElement(doc, response, "umidadeMedia",      String.valueOf(dto.getUmidadeMedia()));
        addElement(doc, response, "precipitacaoTotal", String.valueOf(dto.getPrecipitacaoTotal()));
        addElement(doc, response, "ndviMedio",         String.valueOf(dto.getNdviMedio()));
        addElement(doc, response, "quantidadeAlertas", String.valueOf(dto.getQuantidadeAlertas()));
        addElement(doc, response, "periodoInicio",     dto.getPeriodoInicio());
        addElement(doc, response, "periodoFim",        dto.getPeriodoFim());

        log.info("[SOAP Endpoint] Relatório gerado para fazenda '{}'", dto.getNomeFazenda());
        return response;
    }

    private String getOptionalText(Element parent, String tagName) {
        var nodes = parent.getElementsByTagNameNS(NAMESPACE, tagName);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent() : null;
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        Element el = doc.createElementNS(NAMESPACE, name);
        el.setTextContent(value != null ? value : "");
        parent.appendChild(el);
    }
}
