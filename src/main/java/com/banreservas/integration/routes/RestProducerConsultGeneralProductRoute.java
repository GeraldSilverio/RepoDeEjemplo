package com.banreservas.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.ResponseDto;
import com.banreservas.integration.processors.BussinessLogic.GenerateRequestConsultGeneralProductProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RestProducerConsultGeneralProductRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProducerConsultGeneralProductRoute.class);

    @ConfigProperty(name = "consulta.general.producto.url")
    String urlService;

    @Inject
    GenerateRequestConsultGeneralProductProcessor generateRequestConsultGeneralProductProcessor;

    @Override
    public void configure() throws Exception {

        from("direct:consulta-general-producto")
                .routeId("consulta-general-producto")
                .log(LoggingLevel.INFO, LOGGER, "Iniciando consulta al lineal ConsultaGeneralProducto")

                // Limpia headers que puedan interferir en la llamada HTTP
                .removeHeaders("CamelHttp*")
                .removeHeader("host")

                // Procesar y convertir XML a DTO
                .process(generateRequestConsultGeneralProductProcessor)
                .marshal().json(JsonLibrary.Jackson)

                /// Configura headers HTTP para la llamada
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // Llamada al backend
                .toD(urlService + "?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json(JsonLibrary.Jackson, ResponseDto.class)
                .setProperty("consultGeneralProductResponse", body())
                .log(LoggingLevel.DEBUG, LOGGER, "Respuesta del backend: ${body}")
                .end();
    }
}
