package com.banreservas.integration.model.outbound.InfoTransactionSp.Request;

import java.io.Serializable;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection

public record RequestInfoTransactionDto(String transactionType,
        List<ProductDto> products,
        List<AmountDto> amounts) implements Serializable {

}
