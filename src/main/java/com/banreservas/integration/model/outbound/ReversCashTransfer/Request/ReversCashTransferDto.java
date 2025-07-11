package com.banreservas.integration.model.outbound.ReversCashTransfer.Request;

import java.io.Serializable;

import com.banreservas.integration.model.outbound.CashTransfer.Response.ClientDto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ReversCashTransferDto(
        TransactionDto transaction,
        ClientDto client) implements Serializable {

}
