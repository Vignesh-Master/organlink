package com.organlink.exception;

/**
 * Exception thrown when authentication fails
 * Used for login failures, invalid credentials, account lockouts, etc.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
