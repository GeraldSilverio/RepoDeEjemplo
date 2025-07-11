package com.banreservas.integration.model.outbound.GetValuesList;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public record BodyDto(
        @JsonProperty("listOfValues") List<ValueListResponseDto> value
) implements Serializable {
}
