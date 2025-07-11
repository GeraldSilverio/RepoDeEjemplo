package com.banreservas.integration.processors.BussinessLogic;

import java.util.List;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.banreservas.integration.model.outbound.CashTransfer.Request.IdentificationDto;
import com.banreservas.integration.model.outbound.CashTransfer.Request.TransferenciaEfectivoRequestDto;
import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenerateRequestTransferenciaEfectivoProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRequestTransferenciaEfectivoProcessor.class);

    @ConfigProperty(name = "originAccount.account")
    private String originAccountNumber;
    @ConfigProperty(name = "originAccount.productLine")
    private String originAccountProductLine;
    @ConfigProperty(name = "cost.center")
    private String costCenter;
    @ConfigProperty(name = "cuenta.ahorro")
    private String accountSavingValue;
    @ConfigProperty(name = "cuenta.corriente")
    private String accountCurrentValue;

    @Override
    public void process(Exchange exchange) throws Exception {

        // Obtenemos la respuesta de la consulta del producto.
        ResponseDto consultGeneralProductResponse = exchange.getProperty("consultGeneralProductResponse",
                ResponseDto.class);

        CreditAccountRequestDto originalRequest = exchange.getProperty("originalRequest",CreditAccountRequestDto.class);

        String transactionId = exchange.getProperty("TransactionId", String.class);

        exchange.getIn().setHeader("transaction_id", transactionId);
        exchange.getIn().setHeader("terminal", originalRequest.terminal());
        exchange.getIn().setHeader("SessionId", transactionId);
        exchange.getIn().setHeader("service", "CreditoCuenta");
        exchange.getIn().setHeader("sub_service", "TransferenciaEfectivo_COM");
        exchange.getIn().setHeader("client_version", originalRequest.version());
        exchange.getIn().setHeader("version", originalRequest.version());
        exchange.getIn().setHeader("dateTime", originalRequest.dateTime());
        exchange.getIn().setHeader("channel", originalRequest.consumerId());
        exchange.getIn().setHeader("user", originalRequest.user());

        String description = originalRequest.terminal() + " -" + originalRequest.description() + "- " + "TPD DEPOSITO";

        // Creamos el Request para el servicio de Transferencia Efectivo
        List<IdentificationDto> identifications = List
                .of(
                        new IdentificationDto("130922039", "RNC"),
                        new IdentificationDto("0000067094", "PermId"));


        TransferenciaEfectivoRequestDto transferenciaEfectivoRequestDto = new TransferenciaEfectivoRequestDto(
                consultGeneralProductResponse.body().product().number(),
                originalRequest.amount().amount(),
                originalRequest.amount().currency(),
                originalRequest.product().productLine().equalsIgnoreCase("CuentaAhorro") ? "SV" : "DD",
                originAccountNumber,
                description,
                costCenter,
                originalRequest.product().productLine().equals("CuentaAhorro") ? accountSavingValue : accountCurrentValue,
                originalRequest.consumerId(),
                consultGeneralProductResponse.body().client().fullName(),
                identifications);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transferenciaEfectivoRequestDto);
        logger.info("Request de TransferenciaEfectivoCom formato JSON: {}", json);

        exchange.getIn().setBody(transferenciaEfectivoRequestDto);
    }
}
