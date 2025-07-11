package com.banreservas.integration.model.outbound.ConsultTypeProduct.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record ConsultTypeProductRequestDto(
        @JsonProperty("originProduct") String number,
        @JsonProperty("originProductLine") String productLine
) implements Serializable {
}
