package com.banreservas.integration.model.outbound.CashTransfer.Request;

import java.io.Serializable;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TransferenciaEfectivoRequestDto(
       String destinationAccount,
        Double monto,
        String moneda,
        String originproductLine,
        String OriginProductNumber,
        String descriptions,
        String costcenter,
        String code,
        String type,
        String fullname,
        List<IdentificationDto> identifications) implements Serializable {
}