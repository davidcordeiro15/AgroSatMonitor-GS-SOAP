package com.agrosatmonitor.soap.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * WSDL gerado automaticamente a partir do XSD.
     * Disponível em: http://localhost:8082/ws/agrosatmonitor.wsdl
     */
    @Bean(name = "agrosatmonitor")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema agrosatmonitorSchema) {
        DefaultWsdl11Definition def = new DefaultWsdl11Definition();
        def.setPortTypeName("AgroSatMonitorPort");
        def.setLocationUri("/ws");
        def.setTargetNamespace("http://agrosatmonitor.com/soap");
        def.setSchema(agrosatmonitorSchema);
        return def;
    }

    @Bean
    public XsdSchema agrosatmonitorSchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/agrosatmonitor.xsd"));
    }
}
