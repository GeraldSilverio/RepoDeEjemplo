package com.banreservas.integration.model.outbound.CashTransfer.Request;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record IdentificationDto(
        String number,
        String type) implements Serializable {
}
