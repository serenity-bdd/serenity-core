package net.serenitybdd.core.rest;

import com.google.common.base.Optional;

public enum RestMethod {
    PUT,
    GET,
    POST,
    PATCH,
    OPTIONS,
    HEAD,
    DELETE;

    private static final Optional<RestMethod> NOT_A_REST_METHOD = Optional.absent();

    public static Optional<RestMethod> restMethodCalled(String value) {
        return isRestMethod(value) ? Optional.fromNullable(RestMethod.valueOf(value.toUpperCase())) : NOT_A_REST_METHOD;
    }

    private static boolean isRestMethod(String value) {
        for(RestMethod restMethod : values()) {
            if (value.equalsIgnoreCase(restMethod.name())) {
                return true;
            }
        }
        return false;
    }
}