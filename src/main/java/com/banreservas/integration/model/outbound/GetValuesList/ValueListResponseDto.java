package com.banreservas.integration.model.outbound.GetValuesList;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record ValueListResponseDto(
        @JsonProperty("Comercio") String merchant,
        @JsonProperty("Cuenta") String account,
        @JsonProperty("Terminal") String terminal
) implements Serializable {
}
