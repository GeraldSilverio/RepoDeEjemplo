package com.banreservas.integration.processors.Validations;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.ResponseDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ValidateConsultGeneralProductProcessor implements Processor {

    @ConfigProperty(name = "MensajeCuentaNoActiva")
    String inactiveAccountMessage;

    @ConfigProperty(name = "MonedaCreditoCuenta")
    String currencyValidationMessage;

    @Override
    public void process(Exchange exchange) throws Exception {
        ResponseDto response = exchange.getProperty("consultGeneralProductResponse", ResponseDto.class);

        CreditAccountRequestDto requestDto = exchange.getProperty("originalRequest", CreditAccountRequestDto.class);

        if (response == null || response.header() == null) {
            throw new ValidationException("No se recibió respuesta válida de ConsultaGeneralProducto.");
        }

        if (response.header().responseCode() != 200) {
            throw new ValidationException("Error al consultar ConsultaGeneralProducto: " + response.header().responseMessage());
        }

        if (!response.body().product().status().equals("Activa")) {
            throw new ValidationException(inactiveAccountMessage);
        }

        if (!response.body().product().currency().equalsIgnoreCase(requestDto.product().currency())) {
            throw new ValidationException(currencyValidationMessage);
        }
    }
}
