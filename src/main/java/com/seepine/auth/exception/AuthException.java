package com.seepine.auth.exception;

/**
 * @author seepine
 */
public class AuthException extends Exception {
    String message;

    public AuthException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
