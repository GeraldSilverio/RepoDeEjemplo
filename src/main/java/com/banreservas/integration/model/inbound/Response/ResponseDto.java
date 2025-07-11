package com.banreservas.integration.model.inbound.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record ResponseDto(HeaderDto header, BodyDto body) implements Serializable {
}
