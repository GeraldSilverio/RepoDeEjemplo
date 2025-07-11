package com.banreservas.integration.model.outbound.ConsultTypeProduct.Response;

import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.HeaderDto;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

@RegisterForReflection
public record ConsultTypeProductResponseDto(HeaderDto header, BodyDto body) implements Serializable {
}
