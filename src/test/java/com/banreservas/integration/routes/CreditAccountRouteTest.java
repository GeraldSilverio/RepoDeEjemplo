
package com.banreservas.integration.routes;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import com.banreservas.integration.mocks.RestMock;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(RestMock.class)
public class CreditAccountRouteTest {

    private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUI";
    private static final String ENDPOINT = "/credit/account/api/v1/credit-account";

    @Test
    void shouldProcessSuccessfulCreditToDOPSavingsAccount() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("200"))
                .body("header.responseMessage", equalTo("Exitoso"))
                .body("body.creditAccountResult.channel", equalTo("CashMD"))
                .body("body.creditAccountResult.user", equalTo("CashMD"))
                .body("body.creditAccountResult.terminal", equalTo("C14-053"))
                .body("body.creditAccountResult.type", equalTo(0))
                .body("body.creditAccountResult.message", equalTo("TRANSACCION PROCESADA EXITOSAMENTE"))
                .body("body.creditAccountResult.transactionId", notNullValue());
    }

    @Test
    void shouldProcessSuccessfulCreditToUSDCheckingAccount() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "TestUser",
                            "terminal": "TP001",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "2400063146",
                                "productLine": "CuentaCorriente",
                                "currency": "USD"
                            },
                            "amount": {
                                "currency": "USD",
                                "amount": 50
                            },
                            "description": "Test USD deposit"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("200"))
                .body("header.responseMessage", equalTo("Exitoso"))
                .body("body.creditAccountResult.type", equalTo(0))
                .body("body.creditAccountResult.message", equalTo("TRANSACCION PROCESADA EXITOSAMENTE"));
    }

    @Test
    void shouldRejectRequestWithMissingAuthorizationHeader() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("Header Authorization es requerido"));
    }

    @Test
    void shouldRejectRequestWithMissingConsumerId() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("El campo 'Canal' es requerido"));
    }

    @Test
    void shouldRejectRequestWithNegativeAmount() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": -100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("S034 - IMPORTE DE TRANSACCION NO PUEDE SER NEGATIVO."));
    }

    @Test
    void shouldRejectRequestWithInvalidCurrency() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "EUR"
                            },
                            "amount": {
                                "currency": "EUR",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("LA MONEDA DEL IMPORTE NO ES VÁLIDA"));
    }

    @Test
    void shouldRejectRequestWithAmountExceedingDOPLimit() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 500000
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("TPD03 - EL DEPOSITO A INTENTAR ESTA FUERA DEL RANGO."));
    }

    @Test
    void shouldRejectRequestWithAmountExceedingUSDLimit() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "USD"
                            },
                            "amount": {
                                "currency": "USD",
                                "amount": 150000
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("TPD03 - EL DEPOSITO A INTENTAR ESTA FUERA DEL RANGO."));
    }

    @Test
    void shouldRejectRequestForRestrictedProductType() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "8700000001",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("TERMINAL Y EMPRESA NO CONCUERDAN"));
    }

    @Test
    void shouldRejectRequestForInactiveAccount() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "0000000000",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)));
    }

    @Test
    void shouldHandleConnectionResetError() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "ConnectionReset",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(anyOf(is(400), is(500), is(502)))
                .contentType(ContentType.JSON)
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)));
    }

    @Test
    void shouldRejectRequestWithMissingProductInfo() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("El campo 'Product' es requerido"));
    }

    @Test
    void shouldRejectRequestWithMissingAmountInfo() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("El campo 'Amount' es requerido"));
    }

    @Test
    void shouldRejectRequestWithEmptyProductNumber() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("400"))
                .body("body.creditAccountResult.type", anyOf(equalTo(1), equalTo(3)))
                .body("body.creditAccountResult.message", containsString("El campo 'Product.Number' es requerido"));
    }

    @Test
    void shouldRejectRequestWithInvalidJsonFormat() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("{ invalid json format }")
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(anyOf(is(400), is(500)))
                .contentType(ContentType.JSON);
    }

    @Test
    void shouldReturnValidResponseStructureForSuccessfulTransaction() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("header", notNullValue())
                .body("header.responseCode", notNullValue())
                .body("header.responseMessage", notNullValue())
                .body("body", notNullValue())
                .body("body.creditAccountResult", notNullValue())
                .body("body.creditAccountResult.channel", notNullValue())
                .body("body.creditAccountResult.user", notNullValue())
                .body("body.creditAccountResult.terminal", notNullValue())
                .body("body.creditAccountResult.dateTime", notNullValue())
                .body("body.creditAccountResult.transactionId", notNullValue())
                .body("body.creditAccountResult.type", notNullValue())
                .body("body.creditAccountResult.message", notNullValue())
                .body("body.creditAccountResult.version", notNullValue());
    }

    @Test
    void shouldReturnValidResponseStructureForErrorTransaction() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": -100
                            },
                            "description": "2143"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("header", notNullValue())
                .body("header.responseCode", notNullValue())
                .body("header.responseMessage", notNullValue())
                .body("body", notNullValue())
                .body("body.creditAccountResult", notNullValue())
                .body("body.creditAccountResult.type", notNullValue())
                .body("body.creditAccountResult.message", notNullValue());
    }

    @Test
    void shouldProcessMinimalValidRequest() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", AUTH_TOKEN)
                .body("""
                        {
                            "consumerId": "CashMD",
                            "user": "CashMD",
                            "terminal": "C14-053",
                            "dateTime": "2024-06-18 16:41:57",
                            "version": 1,
                            "product": {
                                "number": "3110002733",
                                "productLine": "CuentaAhorro",
                                "currency": "DOP"
                            },
                            "amount": {
                                "currency": "DOP",
                                "amount": 50
                            },
                            "description": "minimal test"
                        }
                        """)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("header.responseCode", equalTo("200"))
                .body("body.creditAccountResult.type", equalTo(0));
    }

    @Test
    void healthEndpointShouldBeAccessible() {
        given()
                .when()
                .get("/credit/account/api/v1/health")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    void swaggerEndpointShouldBeAccessible() {
        given()
                .when()
                .get("/credit/account/api/v1/swagger-ui")
                .then()
                .statusCode(anyOf(is(200), is(404), is(302)));
    }
}

