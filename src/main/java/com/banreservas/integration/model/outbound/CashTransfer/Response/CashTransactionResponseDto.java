package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;

import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.HeaderDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record CashTransactionResponseDto(
        HeaderDto header,
        BodyDto body) implements Serializable {
}
