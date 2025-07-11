package com.banreservas.integration.processors.BussinessLogic;


import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.outbound.GetValuesList.GetValueListRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenerateRequestGetValueListProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRequestGetValueListProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        logger.info("Creando el request para consultar al lineal ObtenerListaValoresSP");

        // Obtenemos el request original
        CreditAccountRequestDto originalRequest = exchange.getProperty("originalRequest",
                CreditAccountRequestDto.class);

        String transactionId = exchange.getProperty("TransactionId", String.class);

        //Seteando los headers que necesita el lineal
        exchange.getIn().setHeader("user", originalRequest.user());
        exchange.getIn().setHeader("terminal", originalRequest.terminal());
        exchange.getIn().setHeader("dateTime", originalRequest.dateTime());
        exchange.getIn().setHeader("sessionId", transactionId);
        exchange.getIn().setHeader("channel", originalRequest.consumerId());
        exchange.getIn().setHeader("operationName", "CreditoCuenta");

        GetValueListRequestDto getValueListRequestDto = new GetValueListRequestDto("TPD");

        logger.info("Request generado para ObtenerListaValoresSP: {}", getValueListRequestDto);

        //Guardandolo en el exchange para llamarlo en el processor.
        exchange.getIn().setBody(getValueListRequestDto);
    }
}
