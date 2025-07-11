package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;
import java.util.List;

import com.banreservas.integration.model.outbound.CashTransfer.Request.IdentificationDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record ClientDto(
        List<IdentificationDto> identifications,
        String fullName) implements Serializable {

}