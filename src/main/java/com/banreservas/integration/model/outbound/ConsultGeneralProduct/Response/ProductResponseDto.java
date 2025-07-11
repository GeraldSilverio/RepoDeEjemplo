package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProductResponseDto(
        String number,
        String productLine,
        String currency,
        String status,
        ProductDetailDto detail) implements Serializable {

}
