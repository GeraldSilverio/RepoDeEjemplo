package com.banreservas.integration.model.outbound.InfoTransactionSp.Request;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProductDto(
    String order,
    String number,
    String productLine,
    String currency
) implements Serializable{}
