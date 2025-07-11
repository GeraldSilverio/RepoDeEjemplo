package com.banreservas.integration.model.outbound.ReversCashTransfer.Response;

import java.io.Serializable;

import com.banreservas.integration.model.outbound.CashTransfer.Response.ClientDto;
import com.banreservas.integration.model.outbound.CashTransfer.Response.ProductDto;
import com.banreservas.integration.model.outbound.CashTransfer.Response.TransactionDto;
import com.banreservas.integration.model.outbound.InfoTransactionSp.Request.AmountDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record BodyDto(ProductDto product,
        TransactionDto transactionDto,
        ClientDto client,
        AmountDto amount) implements Serializable {
}
