package com.banreservas.integration.processors.Validations;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.outbound.CashTransfer.Response.CashTransactionResponseDto;


public class ValidateCashTransferProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        CashTransactionResponseDto response = exchange.getProperty("cashTransferResponse", CashTransactionResponseDto.class);

        if (response == null || response.header() == null) {
            throw new ValidationException("No se recibió respuesta válida de TransferenciaEfectivoCom.");
        }

        if (response.header().responseCode() != 200) {
            throw new ValidationException("Error al consultar TransferenciaEfectivoCom: " + response.header().responseMessage());
        }
    }
}
