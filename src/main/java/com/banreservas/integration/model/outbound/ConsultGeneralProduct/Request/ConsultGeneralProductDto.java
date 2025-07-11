package com.banreservas.integration.model.outbound.ConsultGeneralProduct.Request;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ConsultGeneralProductDto(ProductDto product) implements Serializable {
    
}
