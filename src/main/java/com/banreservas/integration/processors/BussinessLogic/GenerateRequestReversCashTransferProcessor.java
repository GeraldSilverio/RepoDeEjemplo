package com.banreservas.integration.processors.BussinessLogic;

import java.util.List;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import com.banreservas.integration.model.outbound.CashTransfer.Request.IdentificationDto;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.banreservas.integration.model.outbound.CashTransfer.Response.ClientDto;
import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.ResponseDto;
import com.banreservas.integration.model.outbound.ReversCashTransfer.Request.ReversCashTransferDto;
import com.banreservas.integration.model.outbound.ReversCashTransfer.Request.TransactionDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenerateRequestReversCashTransferProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(GenerateRequestReversCashTransferProcessor.class);

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

        logger.info("Creando el request para el lineal ReversoTransferenciaEfectivoCom");

        // Obtenemos la respuesta de la consulta del lineal ConsultaGeneralProducto.
        ResponseDto consultGeneralProductResponse = exchange.getProperty("consultGeneralProductResponse",
                ResponseDto.class);

        // Obtenemos el request original
        CreditAccountRequestDto originalRequest = exchange.getProperty("originalRequest",
                CreditAccountRequestDto.class);

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

        List<String> descriptions = List.of(description);

        TransactionDto transactionDto = new TransactionDto(
                "NORMAL",
                consultGeneralProductResponse.body().product().number(),
                originalRequest.product().productLine(),
                originalRequest.product().currency(),
                String.valueOf(originalRequest.amount().amount().doubleValue()),
                "COMPLETED",
                originalRequest.product().productLine().equalsIgnoreCase("CuentaAhorro") ? "SV" : "DD",
                originAccountNumber,
                descriptions,
                costCenter,
                originalRequest.product().productLine().equals("CuentaAhorro") ? accountSavingValue : accountCurrentValue,
                originalRequest.consumerId(),
                transactionId);

        // Creamos el Request para el servicio de Transferencia Efectivo
        List<IdentificationDto> identifications = List
                .of(
                        new IdentificationDto("130922039", "RNC"),
                        new IdentificationDto("0000067094", "PermId"));

        ClientDto client = new ClientDto(identifications,consultGeneralProductResponse.body().client().fullName());

        ReversCashTransferDto requestDto = new ReversCashTransferDto(transactionDto, client);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestDto);
        logger.info("Request Generado para el lineal ReversoTransferenciaEfectivoCom: {}", json);

        exchange.getIn().setBody(requestDto);
    }
}
