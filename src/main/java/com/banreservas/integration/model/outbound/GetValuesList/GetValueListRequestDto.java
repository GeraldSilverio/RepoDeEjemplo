package com.banreservas.integration.model.outbound.GetValuesList;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record GetValueListRequestDto(
        String list
) implements Serializable {
}
