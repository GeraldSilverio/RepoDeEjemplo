package com.banreservas.integration.processors.Validations;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.outbound.ConsultTypeProduct.Response.ConsultTypeProductResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ValidateConsultTypeProductProcessor implements Processor {

    @ConfigProperty(name = "ValidacionCuentaInclusivaDebito")
    String notValidAccountMessage;

    @Override
    public void process(Exchange exchange) throws Exception {
        ConsultTypeProductResponseDto response = exchange.getProperty("consultTypeProductResponse", ConsultTypeProductResponseDto.class);

        if (response == null || response.header() == null) {
            throw new ValidationException("No se recibió respuesta válida de ConsultaTipoProducto.");
        }

        if (response.header().responseCode() != 200) {
            throw new ValidationException("Error al consultar ConsultaTipoProducto: " + response.header().responseMessage());
        }

        if (response.body().products().getFirst().productTypeCode().equals("87")) {

            throw new ValidationException(notValidAccountMessage);
        }
    }
}