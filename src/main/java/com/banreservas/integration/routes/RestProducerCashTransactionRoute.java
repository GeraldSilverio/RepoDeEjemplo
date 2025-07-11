package com.banreservas.integration.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.CashTransfer.Response.CashTransactionResponseDto;
import com.banreservas.integration.processors.BussinessLogic.GenerateRequestTransferenciaEfectivoProcessor;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RestProducerCashTransactionRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProducerCashTransactionRoute.class);

    @ConfigProperty(name = "transferencia.efectivo.com.url")
    String urlService;

    @Inject
    GenerateRequestTransferenciaEfectivoProcessor generateRequestTransferenciaEfectivoProcessor;

    @Override
    public void configure() throws Exception {

        from("direct:transferencia-efectivo-com")
                .routeId("transferencia-efectivo-com")
                .log(LoggingLevel.INFO, LOGGER, "Iniciando el llamada a TransferenciaEfectivoCom")

                // Limpia headers que puedan interferir en la llamada HTTP
                .removeHeaders("CamelHttp*")
                .removeHeader("host")

                // Procesar y convertir XML a DTO
                .process(generateRequestTransferenciaEfectivoProcessor)

                .marshal().json(JsonLibrary.Jackson)

                // Llamada al backend
                .toD(urlService + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson, CashTransactionResponseDto.class)
                .setProperty("cashTransferResponse", body())
                .log(LoggingLevel.DEBUG, LOGGER, "Respuesta del backend: ${body}")
                .end();
    }
}
