package com.banreservas.integration.model.outbound.ConsultTypeProduct.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public record BodyDto(
        List<ProductResponseDto> products) implements Serializable {
}
