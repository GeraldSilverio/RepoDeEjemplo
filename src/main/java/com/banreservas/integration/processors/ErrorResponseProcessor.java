package com.banreservas.integration.processors;

import com.banreservas.integration.model.inbound.Response.CreditAccountResponse;
import com.banreservas.integration.util.Constants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;


/**
 * Processor para generar respuestas de error en formato SOAP según el WSDL.
 * Se utiliza cuando ocurren excepciones durante el procesamiento.
 *
 * @author Consultor Domingo Ruiz
 * @since 04/06/2025
 * @version 1.0.0
 */

@ApplicationScoped
public class ErrorResponseProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorResponseProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("Procesando respuesta de error");

        // Obtener el código HTTP del exchange (por defecto 500)
        Integer httpCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        if (httpCode == null) {
            httpCode = 500;
        }

        // Obtener el mensaje de error
        String mensaje = exchange.getProperty(Constants.MESSAGE_PROPERTIE, String.class);
        if (mensaje == null || mensaje.isEmpty()) {
            mensaje = getDefaultMessageForCode(httpCode);
        }
        CreditAccountResponse response = new CreditAccountResponse(
                "",
                "",
                "",
                "",
                "",
                Short.parseShort("1"),
                mensaje,
                ""
        );

        // Establecer el código HTTP en el header
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);

        // Establecer el cuerpo de la respuesta

        // Establecer el código HTTP en el header
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, httpCode);

        // Establecer el cuerpo de la respuesta
        exchange.getIn().setBody(response);

        logger.info("Respuesta de error generada: HTTP {} - {}", httpCode, mensaje);
    }

    private String getDefaultMessageForCode(Integer httpCode) {
        return switch (httpCode) {
            case 400 -> "Request inválido";
            case 401 -> "No autorizado";
            case 403 -> "Acceso denegado";
            case 500 -> "Error interno del servidor";
            case 502 -> "Error en servicio externo";
            case 503 -> "Servicio no disponible";
            default -> "Error en el procesamiento";
        };
    }

}