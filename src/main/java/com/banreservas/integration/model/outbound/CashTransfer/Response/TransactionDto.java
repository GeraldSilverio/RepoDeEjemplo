package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record TransactionDto(
    String name,
    String destinationProductNumber,
    String destinationProductLine,
    String currency,
    double amount,  
    String status,
    String originProductLine,
    String originProductNumber,
    String descriptions,
    String costCenter,
    String code,
    String type
) implements Serializable {}