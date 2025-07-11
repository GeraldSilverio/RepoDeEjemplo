package com.banreservas.integration.routes;

import com.banreservas.integration.model.outbound.ConsultTypeProduct.Response.ConsultTypeProductResponseDto;
import com.banreservas.integration.processors.BussinessLogic.GenerateRequestConsultTypeProductProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class RestProducerConsultTypeProductRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProducerConsultTypeProductRoute.class);

    @ConfigProperty(name = "consulta.tipo.producto.url")
    String urlService;

    @Inject
    GenerateRequestConsultTypeProductProcessor generateRequestConsultTypeProductProcessor;

    @Override
    public void configure() throws Exception {

        from("direct:consulta-tipo-producto")
                .routeId("consulta-tipo-producto")
                .log(LoggingLevel.INFO, LOGGER, "Iniciando consulta al lineal ConsultaTipoProducto")

                .process(generateRequestConsultTypeProductProcessor)

                .removeHeaders("CamelHttp*")
                .removeHeader("host")

                .marshal().json(JsonLibrary.Jackson)

                // Configura headers HTTP y asegúrate de conservar los tuyos
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                .toD(urlService + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson, ConsultTypeProductResponseDto.class)
                .setProperty("consultTypeProductResponse", body())
                .log(LoggingLevel.DEBUG, LOGGER, "Respuesta del backend ConsultaTipoProducto: ${body}")
                .end();
    }
}
