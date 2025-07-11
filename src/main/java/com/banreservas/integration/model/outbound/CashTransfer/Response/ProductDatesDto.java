package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record ProductDatesDto(
    String expiration
)  implements Serializable{}