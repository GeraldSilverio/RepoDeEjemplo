package com.banreservas.integration.model.outbound.InfoTransactionSp.Request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record AmountDto(
        Double amount,
        String currency) implements Serializable {
}
