package com.banreservas.integration.model.outbound.ReversCashTransfer.Request;

import java.io.Serializable;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransactionDto(
        String name,
        String destinationProductNumber,
        String destinationProductLine,
        String currency,
        String amount,
        String status,
        String originProductLine,
        String originProductNumber,
        List<String> descriptions,
        String costCenter,
        String code,
        String type,
        String transactionId) implements Serializable {
}
