package com.banreservas.integration.model.outbound.InfoTransactionSp.Response;

import java.io.Serializable;
import java.util.List;

import com.banreservas.integration.model.outbound.InfoTransactionSp.Request.AmountDto;
import com.banreservas.integration.model.outbound.InfoTransactionSp.Request.ProductDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public record BodyDto(
    String code,
    String message,
    String type,
    String transactionType,
    List<InfoTransactionDto> infoTransaction,
    List<ProductDto> products,
    List<AmountDto> amounts,
    String description,
    String lastUpdateDate
) implements Serializable {}