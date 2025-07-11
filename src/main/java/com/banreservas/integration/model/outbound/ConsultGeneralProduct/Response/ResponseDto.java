package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public record ResponseDto(
        HeaderDto header,
        BodyDto body) implements Serializable {

}
