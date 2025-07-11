package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public record BodyDto(
        ProductResponseDto product,
        ClientDto client) implements Serializable {

}
