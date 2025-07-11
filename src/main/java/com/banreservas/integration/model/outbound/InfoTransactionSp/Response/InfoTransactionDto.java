package com.banreservas.integration.model.outbound.InfoTransactionSp.Response;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record InfoTransactionDto(
        String transactionType,
        String channel,
        String currency,
        String originAccountType,
        String destinyAccountType,
        String accountingAccount,
        String transactionCode,
        String costCenter,
        String accountCurrency,
        String description) implements Serializable {
}