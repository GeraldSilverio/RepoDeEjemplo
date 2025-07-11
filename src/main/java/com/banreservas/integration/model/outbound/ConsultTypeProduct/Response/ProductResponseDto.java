package com.banreservas.integration.model.outbound.ConsultTypeProduct.Response;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record ProductResponseDto(
        String numberProduct,
        String productType,
        String type,
        String productTypeCode
) implements Serializable {
}
