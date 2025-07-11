package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ClientDto(
        List<IdentificationDto> indentification,
        String fullName) implements Serializable {

}
