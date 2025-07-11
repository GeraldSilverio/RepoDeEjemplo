package com.banreservas.integration.model.inbound.Request;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record CreditAccountRequestDto(
        String consumerId,
        String user,
        String terminal,
        String dateTime,
        Integer version,
        ProductRequestDto product,
        AmountRequestDto amount,
        String description
) implements Serializable {

}