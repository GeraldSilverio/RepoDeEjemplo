package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Request;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProductDto(
        String number,
        String productLine,
        String currency) implements Serializable {

}
