package com.banreservas.integration.processors.Validations;

import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.outbound.GetValuesList.GetValuesListResponseDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class ValidateGetValueListProcessor implements Processor {

    @ConfigProperty(name = "TerminalInvalida")
    private String validateTerminal;
    @Override
    public void process(Exchange exchange) throws Exception {
        GetValuesListResponseDto response = exchange.getProperty("getValueListResponse", GetValuesListResponseDto.class);

        if (response == null || response.header() == null) {
            throw new ValidationException("No se recibió respuesta válida de ObtenerListaValoresSP.");
        }

        if (response.header().responseCode() != 200) {
            throw new ValidationException("Error al consultar ObtenerListaValoresSP: " + response.header().responseMessage());
        }

        if(response.body().value() != null && response.body().value().size() > 0){
            CreditAccountRequestDto requestDto = exchange.getProperty("originalRequest", CreditAccountRequestDto.class);

            boolean isValidTerminal = response.body().value().stream()
                    .anyMatch(item -> requestDto.terminal().equals(item.terminal())
                            && requestDto.product().number().equals(item.account()));

            if(!isValidTerminal){
                throw new ValidationException(validateTerminal);

            }

        }
    }
}