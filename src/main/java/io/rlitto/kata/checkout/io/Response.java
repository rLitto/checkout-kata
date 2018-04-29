package io.rlitto.kata.checkout.io;

import java.util.Objects;

public class Response {
    private final String errorMessage;

    protected Response(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static Response success() {
        return new Response(null);
    }

    public static Response error(String message) {
        Objects.requireNonNull(message, "Error message must be not null");
        return new Response(message);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isError() {
        return errorMessage != null;
    }
}
