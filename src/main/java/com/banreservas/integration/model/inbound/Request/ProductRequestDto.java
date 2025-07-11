package com.banreservas.integration.model.inbound.Request;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record ProductRequestDto(
        String number,
        String productLine,
        String currency
) implements Serializable {
}
