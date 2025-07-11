package com.banreservas.integration.processors.Validations;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.banreservas.integration.exceptions.ValidationException;
import com.banreservas.integration.model.outbound.ReversCashTransfer.Response.ReversCashTransferResponseDto;


public class ValidateReversCashProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        ReversCashTransferResponseDto response = exchange.getProperty("reverseCashTransfer", ReversCashTransferResponseDto.class);

        if (response == null || response.header() == null) {
            throw new ValidationException("No se recibió respuesta válida de ReversoTransferenciaEfectivoCom.");
        }

        if (response.header().responseCode() != 200) {
            throw new ValidationException("Error al consultar ReversoTransferenciaEfectivoCom: " + response.header().responseMessage());
        }
    }
}
