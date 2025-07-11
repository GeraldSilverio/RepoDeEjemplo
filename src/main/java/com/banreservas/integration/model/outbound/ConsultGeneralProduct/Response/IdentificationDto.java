package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record IdentificationDto(
        String number,
        String type) implements Serializable {

}
