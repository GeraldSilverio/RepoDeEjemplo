package com.banreservas.integration.model.outbound.GetValuesList;

import com.banreservas.integration.model.outbound.ConsultGeneralProduct.Response.HeaderDto;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public record GetValuesListResponseDto(
        HeaderDto header,
        BodyDto body
) implements Serializable {
}
