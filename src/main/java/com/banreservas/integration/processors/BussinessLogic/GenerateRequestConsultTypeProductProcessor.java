package com.banreservas.integration.processors.BussinessLogic;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.inbound.Request.ProductRequestDto;
import com.banreservas.integration.model.outbound.ConsultTypeProduct.Request.ConsultTypeProductRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GenerateRequestConsultTypeProductProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRequestConsultTypeProductProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        logger.info("Creando el request para consultar al lineal ConsultaTipoProducto");

        // Obtenemos el request original
        CreditAccountRequestDto originalRequest = exchange.getProperty("originalRequest",
                CreditAccountRequestDto.class);

        String transactionId = exchange.getProperty("TransactionId",String.class);

        //Seteando los headers que necesita el lineal
        exchange.getIn().setHeader("id_consumidor", originalRequest.consumerId());
        exchange.getIn().setHeader("usuario", originalRequest.user());
        exchange.getIn().setHeader("terminal", originalRequest.terminal());
        exchange.getIn().setHeader("Fecha_Hora", originalRequest.dateTime());
        exchange.getIn().setHeader("sessionId", transactionId);
        exchange.getIn().setHeader("operacion", "ConsultaGeneralProducto");

        ProductRequestDto productRequest = originalRequest.product();;
        ConsultTypeProductRequestDto consultTypeProductRequestDto = new ConsultTypeProductRequestDto(productRequest.number(),productRequest.productLine());

        logger.info("Request generado para ConsultaTipoProducto: {}", consultTypeProductRequestDto);

        //Guardandolo en el exchange para llamarlo en el processor.
        exchange.getIn().setBody(consultTypeProductRequestDto);
    }
}