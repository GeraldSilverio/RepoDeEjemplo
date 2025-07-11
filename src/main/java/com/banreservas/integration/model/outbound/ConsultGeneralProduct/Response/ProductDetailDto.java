package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProductDetailDto(
    ProductDatesDto dates
) implements Serializable {
}