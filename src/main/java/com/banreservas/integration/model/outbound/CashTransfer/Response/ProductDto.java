package com.banreservas.integration.model.outbound.CashTransfer.Response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;
@JsonIgnoreProperties(ignoreUnknown = true)

@RegisterForReflection
public record ProductDto(
    String order,
    String number,
    String productLine,
    String currency,
    String status,
    String productTypeCode,
    List<ProductDetailDto> details
) implements Serializable{}