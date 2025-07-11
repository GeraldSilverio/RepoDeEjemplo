package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;

import com.banreservas.integration.model.outbound.InfoTransactionSp.Request.AmountDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record BodyDto(
    ProductDto product,
    TransactionDto transaction,
    ClientDto client,
    AmountDto amount,
    String rqUID
) implements Serializable{}
