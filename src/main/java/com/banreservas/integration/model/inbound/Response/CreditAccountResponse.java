package com.banreservas.integration.model.inbound.Response;

import java.io.Serializable;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record CreditAccountResponse(
        String channel,
        String user,
        String terminal,
        String dateTime,
        String transactionId,
        short type,
        String message,
        String version
) implements Serializable {}
