package com.banreservas.integration.routes;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.inbound.Response.BodyDto;
import com.banreservas.integration.model.inbound.Response.CreditAccountResponse;
import com.banreservas.integration.model.inbound.Response.HeaderDto;
import com.banreservas.integration.model.inbound.Response.ResponseDto;
import com.banreservas.integration.processors.Validations.*;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class CreditAccountRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CreditAccountRoute.class);

    @Inject
    private ValidateRequestProcessor validateRequestProcessor;

    @Inject
    private ValidateConsultGeneralProductProcessor validateConsultGeneralProductProcessor;

    @Inject
    private ValidateConsultTypeProductProcessor validateConsultTypeProductProcessor;

    @Inject
    private  ValidateGetValueListProcessor validateGetValueListProcessor;

    @Override
    public void configure() throws Exception {

        // Manejo de errores de validación
        onException(ValidationException.class)
                .handled(true)
                .log(LoggingLevel.WARN, logger, "Error de validación en orquestador: ${exception.message}")
                .process(exchange -> buildErrorResponse(exchange,
                        exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage(),
                        (short) 1, 400, "VALIDATION_ERROR"))
                .marshal().json(JsonLibrary.Jackson)
                .end();

        // Manejo genérico de errores
        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, logger, "Error inesperado en orquestador: ${exception.message}")
                .process(exchange -> buildErrorResponse(exchange,
                        exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).getMessage(),
                        (short) 1, 400, "ERROR"))
                .marshal().json(JsonLibrary.Jackson)
                .end();

        restConfiguration()
                .component("platform-http")
                .contextPath("credit/account")
                .bindingMode(RestBindingMode.json)
                .apiProperty("api.title", "Orquestador aplicar credito a cuenta")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true")
                .apiProperty("prettyPrint", "true");

        rest("/api/v1")
                .post("credit-account")
                .type(CreditAccountRequestDto.class)
                .to("direct:aplicaCreditoCuenta");

        from("direct:aplicaCreditoCuenta")
                .routeId("apply-credit-account-orchestrator")
                .log(LoggingLevel.INFO, logger, "Orquestación iniciada")

                .setProperty("originalRequest", body())
                .log(LoggingLevel.INFO, logger, "Validando campos del request")
                .process(validateRequestProcessor)

                .process(exchange -> {
                    String transactionId = generateTransactionId();
                    exchange.setProperty("TransactionId", transactionId);
                    exchange.getIn().setHeader("TransactionId", transactionId);
                })
                .log(LoggingLevel.INFO, logger, "ID generado para transacción: ${exchangeProperty.TransactionId}")

                .log(LoggingLevel.INFO, logger, "Llamando a Obtener Lista de Valores")
                .to("direct:obtener-lista-valores")
                .process(validateGetValueListProcessor)

                .log(LoggingLevel.INFO, logger, "Llamando a ConsultaGeneralProducto")
                .to("direct:consulta-general-producto")
                .process(validateConsultGeneralProductProcessor)

                .log(LoggingLevel.INFO, logger, "Llamando a ConsultaTipoProducto")
                .to("direct:consulta-tipo-producto")
                .process(validateConsultTypeProductProcessor)

                .log(LoggingLevel.INFO, logger, "Llamando a TransferenciaEfectivoCom")
                .to("direct:transferencia-efectivo-com")
                .process(new ValidateCashTransferProcessor())

                // Validar status code 500 para ejecutar reverso
                .process(exchange -> {
                    Integer httpResponseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
                    if (httpResponseCode != null && httpResponseCode == 500 || httpResponseCode == 502 || httpResponseCode == 503 || httpResponseCode == 504) {
                        log.info("Status code 500 detectado, ejecutando reverso de transferencia efectivo");
                        exchange.setProperty("executeReversal", true);
                    } else {
                        exchange.setProperty("executeReversal", false);
                    }
                })

                // Ejecutar reverso solo si es necesario
                /*.choice()
                .when(exchangeProperty("executeReversal").isEqualTo(true))
                .log(LoggingLevel.WARN, logger, "Ejecutando reverso por status code 500")*/
                .to("direct:reverso-transferencia-efectivo")
                /*.otherwise()
                .log(LoggingLevel.INFO, logger, "No se requiere reverso, continuando flujo normal")
                .end()*/

                .log(LoggingLevel.INFO, logger, "Construyendo respuesta final para cliente")
                .process(exchange -> buildSuccessfulResponse(exchange,
                        "TRANSACCION PROCESADA EXITOSAMENTE", (short) 0, 200))
                .end()

                .setHeader("Content-Type", constant("application/json"))
                .end();
    }

    private void buildErrorResponse(Exchange exchange, String message, short tipo, int httpStatus, String errorCode) {

        String consumerId = "";
        String user = "";
        String terminal = "";
        String dateTime = "";
        String version = "";

        CreditAccountResponse response = new CreditAccountResponse(
                consumerId, user, terminal, dateTime, "", tipo, message, version
        );

        ResponseDto responseDto = new ResponseDto(
                new HeaderDto(String.valueOf(httpStatus), getHttpStatusText(httpStatus)),
                new BodyDto(response)
        );

        if(message.contains("Error al consultar ObtenerListaValoresSP: Credenciales inválidas")){
            ResponseDto unauthorizedResponse = new ResponseDto(
                    new HeaderDto(String.valueOf(401), getHttpStatusText(401)),
                    new BodyDto(response)
            );
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
            exchange.getMessage().setHeader("X-Error-Code","Unauthorized" );
            exchange.getMessage().setBody(unauthorizedResponse);
        }else{
            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatus);
            exchange.getMessage().setHeader("X-Error-Code", errorCode);
            exchange.getMessage().setBody(responseDto);
        }

    }

    private void buildSuccessfulResponse(Exchange exchange, String message, short tipo, int httpStatus) {
        CreditAccountRequestDto requestDto = exchange.getProperty("originalRequest", CreditAccountRequestDto.class);
        CreditAccountResponse result = new CreditAccountResponse(
                requestDto.consumerId(), requestDto.user(), requestDto.terminal(),
                requestDto.dateTime(), exchange.getProperty("TransactionId", String.class),
                tipo, message, requestDto.version().toString());
        ResponseDto responseDto = new ResponseDto(
                new HeaderDto(String.valueOf(httpStatus), "Exitoso"),
                new BodyDto(result)
        );
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
        exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, httpStatus);
        exchange.getMessage().setBody(responseDto);
    }

    private String getHttpStatusText(int httpStatus) {
        switch (httpStatus) {
            case 200:
                return "OK";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 503:
                return "Service Unavailable";
            case 504:
                return "Gateway Timeout";
            default:
                return "Error";
        }
    }

    private String generateTransactionId() {
        String timePart = String.valueOf(System.currentTimeMillis());
        // Tomar los últimos 9 dígitos del timestamp
        String timeSubstring = timePart.substring(timePart.length() - 9);

        // Generar 4 dígitos aleatorios (1000-9999)
        int randomPart = (int) (Math.random() * 9000) + 1000;

        String transactionId = timeSubstring + randomPart;

        // Asegurar que nunca empiece por 0
        if (transactionId.charAt(0) == '0') {
            transactionId = "1" + transactionId.substring(1);
        }

        return transactionId;
    }
}