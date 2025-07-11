package com.banreservas.integration.model.inbound.Request;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record AmountRequestDto(
        String currency,
        Double amount
) implements Serializable {
}
