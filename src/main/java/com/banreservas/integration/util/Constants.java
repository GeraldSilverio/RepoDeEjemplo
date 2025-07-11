package com.banreservas.integration.util;

public class Constants {
    public static final String HEADER_CODE_RESPONSE = "CODE_RESPONSE";
    public static final String HEADER_MESSAGE_RESPONSE = "MESSAGE_RESPONSE";
    public static final String HEADER_MSG_TYPE = "msgType";
    public static final String ROUTE_ERROR_500 = "direct:error-500";
    public static final String CODE_INTERNAL_ERROR = "500";
    public static final String CODE_OK_REQUEST = "200";
    public static final String MESSAGE_INTERNAL_ERROR = "Internal server error";
    public static final String MESSAGE_PROPERTIE = "Mensaje";
    public static final String MESSAGE_RESPONSE_ERROR_SERVICE = "errorConsume";
    public static final String MESSAGE_RESPONSE_CONSUME= "validateError";
    private Constants() {
    }
}
