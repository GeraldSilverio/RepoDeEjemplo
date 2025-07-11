package com.banreservas.integration.processors.Validations;

import com.banreservas.integration.model.inbound.Request.CreditAccountRequestDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.banreservas.integration.exceptions.ValidationException;

import java.util.Set;

@ApplicationScoped
public class ValidateRequestProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ValidateRequestProcessor.class);

    private static final Set<String> MONEDAS_PERMITIDAS = Set.of("DOP", "USD");

    @ConfigProperty(name = "monto.minimo.dop")
    String amountMinDop;

    @ConfigProperty(name = "monto.maximo.dop")
    String amountMaxDop;

    @ConfigProperty(name = "monto.minimo.usd")
    String amountMinUsd;

    @ConfigProperty(name = "monto.maximo.usd")
    String amountMaxUsd;

    @ConfigProperty(name = "ImporteNegativo")
    String negativeAmountMessage;

    @ConfigProperty(name = "RangoCreditoTPD")
    String rangeValidationMessage;

    @ConfigProperty(name = "ValidarRangoMonto")
    Boolean validateAmountRange;

    @ConfigProperty(name = "MonedaCreditoCuenta")
    String equaldCurrencyMessage;

    /**
     * Punto de entrada principal para validar todo el cuerpo del request.
     */

    @Override
    public void process(Exchange exchange) throws Exception {

        CreditAccountRequestDto dto = exchange.getProperty("originalRequest", CreditAccountRequestDto.class);

        if(dto == null){
            throwValidation(exchange,"El cuerpo del request no puede ser nulo");
        }
        //Validar el Header que venga el Authorization

        getHeaderOrThrow(exchange, "Authorization", "Header Authorization es requerido");

        // Validar campos básicos
        validateField(dto.consumerId(), "Canal", exchange);
        validateField(dto.user(), "Usuario", exchange);
        validateField(dto.terminal(), "Terminal", exchange);
        validateField(String.valueOf(dto.version()), "Version", exchange);
        validateField(dto.dateTime(), "FechaHora", exchange);

        // Validar producto
        if (dto.product() == null) {
            throwValidation(exchange, "El campo 'Product' es requerido.");
        }
        validateField(dto.product().number(), "Product.Number", exchange);
        validateField(dto.product().productLine(), "Product.ProductLine", exchange);
        validateField(dto.product().currency(), "Product.Currency", exchange);

        // Validar importe
        if (dto.amount() == null) {
            throwValidation(exchange, "El campo 'Amount' es requerido.");
        }
        validateField(dto.amount().currency(), "Amount.Currency", exchange);

        //Validando que este dentro de las monedas permitidas.
        validateCurrency(dto.amount().currency(), exchange);

        //Validando el monto no sea negativo, siguiendo la validacion ImporteNegativo
        validateAmount(dto.amount().amount(), exchange);

        //Validando el rango de los montos, siguiendo la validacion RangoCreditoTPD
        validateRange(dto.amount().currency(), dto.amount().amount(), exchange);
    }

    /**
     * Valida que un campo no sea nulo ni vacío.
     */
    private void validateField(String value, String fieldName, Exchange exchange) {
        if (value == null || value.trim().isEmpty()) {
            throwValidation(exchange, "El campo '" + fieldName + "' es requerido.");
        }
    }

    /**
     * Valida que la moneda sea DOP o USD.
     */
    private void validateCurrency(String currency, Exchange exchange) {
        if (!MONEDAS_PERMITIDAS.contains(currency.toUpperCase())) {
            throwValidation(exchange, "LA MONEDA DEL IMPORTE NO ES VÁLIDA.");
        }
        CreditAccountRequestDto requestDto = exchange.getProperty("originalRequest", CreditAccountRequestDto.class);

        if (!requestDto.amount().currency().equalsIgnoreCase(requestDto.product().currency())) {
            throwValidation(exchange, equaldCurrencyMessage);
        }
    }

    /**
     * Valida que el monto no sea negativo.
     */
    private void validateAmount(double monto, Exchange exchange) {
        if (monto < 0) {
            throwValidation(exchange, negativeAmountMessage);
        }
    }

    /**
     * Valida que el monto esté dentro del rango configurado según la moneda.
     */
    private void validateRange(String currency, double amount, Exchange exchange) {
        double min = 0;
        double max = 0;

        switch (currency.toUpperCase()) {
            case "DOP":
                min = Double.parseDouble(amountMinDop);
                max = Double.parseDouble(amountMaxDop);
                break;
            case "USD":
                min = Double.parseDouble(amountMinUsd);
                max = Double.parseDouble(amountMaxUsd);
                break;
        }

        //Valida el rango dependiendo de la variable de Entorno ValidarRangoMonto
        logger.info("Valor rango" + validateAmountRange);
        if (validateAmountRange) {
            logger.info("Validando rango de transaccion");
            logger.info("Valor rango: " + validateAmountRange);
            logger.info("Monto Minimo permitido: " + min);
            logger.info("Monto Maximo permitido: " + max);
            logger.info("Monto Actual: " + amount);
            if (amount < min || amount > max) {

                throwValidation(exchange, rangeValidationMessage);
            }
        }
    }

    /**
     * Lanza una excepción personalizada con mensaje de validación.
     */
    private void throwValidation(Exchange ex, String msg) {
        logger.warn("{}", msg);
        ex.setProperty("Mensaje", msg);
        throw new ValidationException(msg);
    }

    // Métodos auxiliares

    private String getHeaderOrThrow(Exchange ex, String headerName, String errorMsg) {
        String val = ex.getIn().getHeader(headerName, String.class);
        if (val == null || val.isEmpty())
            throwValidation(ex, errorMsg);
        return val;
    }
}



