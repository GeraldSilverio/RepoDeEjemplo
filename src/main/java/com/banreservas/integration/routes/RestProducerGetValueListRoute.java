package com.banreservas.integration.routes;

import com.banreservas.integration.model.inbound.Response.BodyDto;
import com.banreservas.integration.model.inbound.Response.CreditAccountResponse;
import com.banreservas.integration.model.inbound.Response.HeaderDto;
import com.banreservas.integration.model.inbound.Response.ResponseDto;
import com.banreservas.integration.model.outbound.GetValuesList.GetValuesListResponseDto;
import com.banreservas.integration.processors.BussinessLogic.GenerateRequestGetValueListProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;

@ApplicationScoped
public class RestProducerGetValueListRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestProducerGetValueListRoute.class);

    @ConfigProperty(name = "obtener.lista.valores.url")
    String urlService;

    @ConfigProperty(name = "obtener.lista.valores.timeout", defaultValue = "30000")
    String httpTimeout;

    @Inject
    GenerateRequestGetValueListProcessor generateRequestGetValueListProcessor;

    @Override
    public void configure() throws Exception {
        onException(SocketTimeoutException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, LOGGER, "Timeout al consultar servicio Obtener Lista Valores: ${exception.message}")
                .process(exchange -> {
                    String consumerId = "";
                    String user = "";
                    String terminal = "";
                    String dateTime = "";
                    String version = "";

                    CreditAccountResponse response = new CreditAccountResponse(
                            consumerId, user, terminal, dateTime, "", Short.parseShort("1"), "Timeout al consultar servicio Obtener Lista Valores", version
                    );
                    ResponseDto responseDto = new ResponseDto(
                            new HeaderDto(String.valueOf(504), "Timeout al consultar servicio Obtener Lista Valores"),
                            new BodyDto(response)
                    );
                    // Serializa a JSON con Jackson
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = objectMapper.writeValueAsString(responseDto);

                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 504);
                    exchange.getIn().setBody(jsonResponse);
                })
                .end();

        from("direct:obtener-lista-valores")
                .routeId("obtener-lista-valores")
                .log(LoggingLevel.INFO, LOGGER, "Iniciando consulta al lineal obtener lista valores")

                .removeHeaders("CamelHttp*")
                .removeHeader("host")

                .process(generateRequestGetValueListProcessor)
                .marshal().json(JsonLibrary.Jackson)

                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))

                // Configuración de timeout y otros parámetros HTTP
                .toD(urlService + "?bridgeEndpoint=true&throwExceptionOnFailure=false" +
                        "&connectTimeout=" + httpTimeout +
                        "&socketTimeout=" + httpTimeout)

                .unmarshal().json(JsonLibrary.Jackson, GetValuesListResponseDto.class)
                .setProperty("getValueListResponse", body())
                .log(LoggingLevel.DEBUG, LOGGER, "Respuesta backend ObtenerListaValores procesada exitosamente")
                .end();
    }
}