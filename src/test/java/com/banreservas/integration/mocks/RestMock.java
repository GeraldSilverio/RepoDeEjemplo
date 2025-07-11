package com.banreservas.integration.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RestMock implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        // Configurar stubs de mayor a menor prioridad
        stubSpecificSuccessfulFlows();
        stubSpecificValidationErrors();
        stubConnectivityErrors();
        stubGenericResponses();

        return Map.of(
                "obtener.lista.valores.url", "http://localhost:8089/api/v1/obtener-lista-valores",
                "consulta.tipo.producto.url", "http://localhost:8089/api/v1/consulta-tipo-producto",
                "consulta.general.producto.url", "http://localhost:8089/api/v1/consulta-general-producto",
                "transferencia.efectivo.com.url", "http://localhost:8089/api/v1/transferencia-efectivo",
                "reverso.transferencia.efectivo.com.url", "http://localhost:8089/api/v1/ms-reverso-transferencia-efectivo"
        );
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private void stubSpecificSuccessfulFlows() {
        // Stub completamente permisivo para obtener-lista-valores
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/obtener-lista-valores"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "listOfValues": [
                                            {
                                                "Comercio": "Carrefour Duarte",
                                                "Cuenta": "3110002733",
                                                "Terminal": "C14-053"
                                            },
                                            {
                                                "Comercio": "TEST DOP Checking",
                                                "Cuenta": "2400063146",
                                                "Terminal": "TP001"
                                            },
                                            {
                                                "Comercio": "Other Store",
                                                "Cuenta": "9999999999",
                                                "Terminal": "INVALID-TERMINAL"
                                            }
                                        ]
                                    }
                                }
                                """)));

        // Stub completamente permisivo para consulta-tipo-producto
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-tipo-producto"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "products": [
                                            {
                                                "numberProduct": "3110002733",
                                                "productType": "Savings",
                                                "type": "Regular",
                                                "productTypeCode": "10"
                                            }
                                        ]
                                    }
                                }
                                """)));

        // Stub completamente permisivo para consulta-general-producto  
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/consulta-general-producto"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "detail": {
                                                "dates": {
                                                    "maturity": "2025-12-31"
                                                }
                                            }
                                        },
                                        "client": {
                                            "indentification": [
                                                {
                                                    "number": "40200000000",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        }
                                    }
                                }
                                """)));

        // Stub completamente permisivo para transferencia-efectivo
        wireMockServer.stubFor(post(urlPathEqualTo("/api/v1/transferencia-efectivo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "order": "DESTINO",
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "productTypeCode": "10",
                                            "details": []
                                        },
                                        "transaction": {
                                            "name": "NORMAL",
                                            "destinationProductNumber": "3110002733",
                                            "destinationProductLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "amount": 100.0,
                                            "status": "COMPLETED",
                                            "originProductLine": "GL",
                                            "originProductNumber": "1770110435",
                                            "descriptions": "TPD DEPOSITO",
                                            "costCenter": "45240",
                                            "code": "AH26",
                                            "type": "CashMD"
                                        },
                                        "client": {
                                            "identifications": [
                                                {
                                                    "number": "130922039",
                                                    "type": "RNC"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        },
                                        "amount": {
                                            "amount": 100.0,
                                            "currency": "DOP"
                                        },
                                        "rqUID": "TX123456"
                                    }
                                }
                                """)));

        // Stub universal para reverso-transferencia-efectivo
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/ms-reverso-transferencia-efectivo"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "order": "DESTINO",
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "productTypeCode": "10",
                                            "details": []
                                        },
                                        "transactionDto": {
                                            "name": "NORMAL",
                                            "destinationProductNumber": "3110002733",
                                            "destinationProductLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "amount": 100.0,
                                            "status": "COMPLETED",
                                            "originProductLine": "GL",
                                            "originProductNumber": "1770110435",
                                            "descriptions": "TPD DEPOSITO",
                                            "costCenter": "45240",
                                            "code": "AH26",
                                            "type": "CashMD"
                                        },
                                        "client": {
                                            "identifications": [
                                                {
                                                    "number": "130922039",
                                                    "type": "RNC"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        },
                                        "amount": {
                                            "amount": 100.0,
                                            "currency": "DOP"
                                        }
                                    }
                                }
                                """)));

        // Flujo exitoso para USD Checking Account (2400063146 + TP001)  
        // (Ya no necesitamos stub específico aquí, lo manejamos en el stub genérico de arriba)

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-tipo-producto"))
                .withRequestBody(containing("\"number\":\"2400063146\""))
                .withRequestBody(containing("\"productLine\":\"CuentaCorriente\""))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "products": [
                                            {
                                                "numberProduct": "2400063146",
                                                "productType": "Checking",
                                                "type": "Regular",
                                                "productTypeCode": "20"
                                            }
                                        ]
                                    }
                                }
                                """)));

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-general-producto"))
                .withRequestBody(containing("\"number\":\"2400063146\""))
                .withRequestBody(containing("\"productLine\":\"CuentaCorriente\""))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "number": "2400063146",
                                            "productLine": "CuentaCorriente",
                                            "currency": "USD",
                                            "status": "Activa",
                                            "detail": {
                                                "dates": {
                                                    "maturity": "2025-12-31"
                                                }
                                            }
                                        },
                                        "client": {
                                            "indentification": [
                                                {
                                                    "number": "40200000001",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "fullName": "Maria Rodriguez"
                                        }
                                    }
                                }
                                """)));

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/transferencia-efectivo"))
                .withRequestBody(containing("\"destinationAccount\":\"2400063146\""))
                .withRequestBody(containing("\"monto\":50"))
                .atPriority(1)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "order": "DESTINO",
                                            "number": "2400063146",
                                            "productLine": "CuentaCorriente",
                                            "currency": "USD",
                                            "status": "Activa",
                                            "productTypeCode": "20",
                                            "details": []
                                        },
                                        "transaction": {
                                            "name": "NORMAL",
                                            "destinationProductNumber": "2400063146",
                                            "destinationProductLine": "CuentaCorriente",
                                            "currency": "USD",
                                            "amount": 50.0,
                                            "status": "COMPLETED",
                                            "originProductLine": "GL",
                                            "originProductNumber": "1770110435",
                                            "descriptions": "TP001 - Test USD deposit - TPD DEPOSITO",
                                            "costCenter": "45240",
                                            "code": "CC20",
                                            "type": "CashMD"
                                        },
                                        "client": {
                                            "identifications": [
                                                {
                                                    "number": "130922039",
                                                    "type": "RNC"
                                                }
                                            ],
                                            "fullName": "Maria Rodriguez"
                                        },
                                        "amount": {
                                            "amount": 50.0,
                                            "currency": "USD"
                                        },
                                        "rqUID": "TX123457"
                                    }
                                }
                                """)));
    }

    private void stubSpecificValidationErrors() {
        // Error: Terminal y cuenta no concuerdan (INVALID-TERMINAL con cuenta 9999999999)
        // Ya no necesitamos stub específico, se maneja en el procesador de validación del orquestador

        // Error: Producto tipo 87 (no soporta transacción)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-tipo-producto"))
                .withRequestBody(containing("\"number\":\"8700000001\""))
                .atPriority(2)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "products": [
                                            {
                                                "numberProduct": "8700000001",
                                                "productType": "Restricted",
                                                "type": "Special",
                                                "productTypeCode": "87"
                                            }
                                        ]
                                    }
                                }
                                """)));

        // Error: Producto inactivo (0000000000)
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-general-producto"))
                .withRequestBody(containing("\"number\":\"0000000000\""))
                .atPriority(2)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "number": "0000000000",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Inactiva",
                                            "detail": {
                                                "dates": {
                                                    "maturity": "2025-12-31"
                                                }
                                            }
                                        },
                                        "client": {
                                            "indentification": [
                                                {
                                                    "number": "00000000000",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "fullName": "Cliente Inactivo"
                                        }
                                    }
                                }
                                """)));
    }

    private void stubConnectivityErrors() {
        // Timeout en obtener lista valores
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/obtener-lista-valores"))
                .withRequestBody(containing("TimeoutError"))
                .atPriority(3)
                .willReturn(aResponse()
                        .withStatus(503)
                        .withFixedDelay(5000)));

        // Connection reset en consulta general producto  
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-general-producto"))
                .withRequestBody(containing("ConnectionReset"))
                .atPriority(3)
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        // Unknown host error en transferencia efectivo
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/transferencia-efectivo"))
                .withRequestBody(containing("UnknownHost"))
                .atPriority(3)
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));
    }

    private void stubGenericResponses() {
        // Respuesta genérica exitosa para obtener lista valores - incluye todas las entradas necesarias
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/obtener-lista-valores"))
                .atPriority(8)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "listOfValues": [
                                            {
                                                "Comercio": "Carrefour Duarte",
                                                "Cuenta": "3110002733",
                                                "Terminal": "C14-053"
                                            },
                                            {
                                                "Comercio": "TEST DOP Checking",
                                                "Cuenta": "2400063146",
                                                "Terminal": "TP001"
                                            },
                                            {
                                                "Comercio": "Other Store",
                                                "Cuenta": "1111111111",
                                                "Terminal": "OTHER-TERMINAL"
                                            }
                                        ]
                                    }
                                }
                                """)));

        // Respuesta genérica exitosa para consulta tipo producto
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-tipo-producto"))
                .atPriority(8)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "products": [
                                            {
                                                "numberProduct": "3110002733",
                                                "productType": "Savings",
                                                "type": "Regular",
                                                "productTypeCode": "10"
                                            }
                                        ]
                                    }
                                }
                                """)));

        // Respuesta genérica exitosa para consulta general producto
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/consulta-general-producto"))
                .atPriority(8)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "detail": {
                                                "dates": {
                                                    "maturity": "2025-12-31"
                                                }
                                            }
                                        },
                                        "client": {
                                            "indentification": [
                                                {
                                                    "number": "40200000000",
                                                    "type": "Cedula"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        }
                                    }
                                }
                                """)));

        // Respuesta genérica exitosa para transferencia efectivo
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/transferencia-efectivo"))
                .atPriority(8)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "order": "DESTINO",
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "productTypeCode": "10",
                                            "details": []
                                        },
                                        "transaction": {
                                            "name": "NORMAL",
                                            "destinationProductNumber": "3110002733",
                                            "destinationProductLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "amount": 100.0,
                                            "status": "COMPLETED",
                                            "originProductLine": "GL",
                                            "originProductNumber": "1770110435",
                                            "descriptions": "Generic TPD DEPOSITO",
                                            "costCenter": "45240",
                                            "code": "AH26",
                                            "type": "CashMD"
                                        },
                                        "client": {
                                            "identifications": [
                                                {
                                                    "number": "130922039",
                                                    "type": "RNC"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        },
                                        "amount": {
                                            "amount": 100.0,
                                            "currency": "DOP"
                                        },
                                        "rqUID": "TX123456"
                                    }
                                }
                                """)));

        // Respuesta genérica exitosa para reverso transferencia efectivo
        wireMockServer.stubFor(post(urlEqualTo("/api/v1/ms-reverso-transferencia-efectivo"))
                .atPriority(8)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 200,
                                        "responseMessage": "Exitoso"
                                    },
                                    "body": {
                                        "product": {
                                            "order": "DESTINO",
                                            "number": "3110002733",
                                            "productLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "status": "Activa",
                                            "productTypeCode": "10",
                                            "details": []
                                        },
                                        "transactionDto": {
                                            "name": "NORMAL",
                                            "destinationProductNumber": "3110002733",
                                            "destinationProductLine": "CuentaAhorro",
                                            "currency": "DOP",
                                            "amount": 100.0,
                                            "status": "COMPLETED",
                                            "originProductLine": "GL",
                                            "originProductNumber": "1770110435",
                                            "descriptions": "Generic TPD DEPOSITO",
                                            "costCenter": "45240",
                                            "code": "AH26",
                                            "type": "CashMD"
                                        },
                                        "client": {
                                            "identifications": [
                                                {
                                                    "number": "130922039",
                                                    "type": "RNC"
                                                }
                                            ],
                                            "fullName": "Juan Perez"
                                        },
                                        "amount": {
                                            "amount": 100.0,
                                            "currency": "DOP"
                                        }
                                    }
                                }
                                """)));

        // Catch-all para requests no específicos (baja prioridad)
        wireMockServer.stubFor(post(urlMatching("/api/v1/.*"))
                .atPriority(10)
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "header": {
                                        "responseCode": 500,
                                        "responseMessage": "Error interno del servidor"
                                    }
                                }
                                """)));
    }
}
