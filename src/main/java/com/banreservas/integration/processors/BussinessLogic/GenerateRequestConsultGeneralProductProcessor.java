package com.banreservas.integration.processors.BussinessLogic;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.inbound.Request.ProductRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Request.ConsultGeneralProductDto;
import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Request.ProductDto;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenerateRequestConsultGeneralProductProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRequestConsultGeneralProductProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        logger.info("Creando el request para consultar al lineal ConsultaGeneralProducto");

        // Obtenemos el request original
        CreditAccountRequestDto originalRequest = exchange.getProperty("originalRequest",
                CreditAccountRequestDto.class);

        String transactionId = exchange.getProperty("TransactionId",String.class);

        //Seteando los headers que necesita el lineal
        exchange.getIn().setHeader("Id_consumidor", originalRequest.consumerId());
        exchange.getIn().setHeader("Usuario", originalRequest.user());
        exchange.getIn().setHeader("Terminal", originalRequest.terminal());
        exchange.getIn().setHeader("Fecha_Hora", originalRequest.dateTime());
        exchange.getIn().setHeader("SessionId", transactionId);
        exchange.getIn().setHeader("Canal", originalRequest.consumerId());
        exchange.getIn().setHeader("Operacion", "ConsultaGeneralProducto");

        ProductRequestDto productRequest = originalRequest.product();
        //Creando los dtos que se enviaran en el request al lineal.
        ProductDto productDto = new ProductDto(productRequest.number(), productRequest.productLine(), productRequest.currency());
        ConsultGeneralProductDto consultGeneralProductDto = new ConsultGeneralProductDto(productDto);

        logger.info("Request generado para ConsultaGeneralProductoo: {}", consultGeneralProductDto);

        //Guardandolo en el exchange para llamarlo en el processor.
        exchange.getIn().setBody(consultGeneralProductDto);
    }
}
