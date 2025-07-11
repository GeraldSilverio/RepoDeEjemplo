package com.banreservas.integration.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.banreservas.integration.model.outbound.ReversCashTransfer.Response.ReversCashTransferResponseDto;
import com.banreservas.integration.processors.BussinessLogic.GenerateRequestReversCashTransferProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RestProducerReversCashTransferRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProducerReversCashTransferRoute.class);

    @ConfigProperty(name = "reverso.transferencia.efectivo.com.url")
    String urlService;

    @Inject
    GenerateRequestReversCashTransferProcessor generateRequestReversCashTransferProcessor;

    @Override
    public void configure() throws Exception {

        from("direct:reverso-transferencia-efectivo")
                .routeId("reverso-transferencia-efectivo")
                .log(LoggingLevel.INFO, LOGGER, "Iniciando el llamada a ReversoTransferenciaEfectivo")

                // Limpia headers que puedan interferir en la llamada HTTP
                .removeHeaders("CamelHttp*")
                .removeHeader("host")

                // Procesar y convertir XML a DTO
                .process(generateRequestReversCashTransferProcessor)

                .marshal().json(JsonLibrary.Jackson)

                // Llamada al backend
                .toD(urlService + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson, ReversCashTransferResponseDto.class)
                .setProperty("reverseCashTransfer", body())
                .log(LoggingLevel.DEBUG, LOGGER, "Respuesta del backend: ${body}")
                .end();
    }
}
