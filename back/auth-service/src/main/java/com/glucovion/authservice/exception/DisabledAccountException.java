package com.glucovion.authservice.exception;

/**
 * Exception thrown when a user attempts to authenticate
 * with a disabled account.
 *
 * <p>This is typically used in authentication flows to
 * block access for users marked as inactive.</p>
 */
public class DisabledAccountException extends RuntimeException {

    /**
     * Constructs a new DisabledAccountException with the specified message.
     *
     * @param message the detail message
     */
    public DisabledAccountException(String message) {
        super(message);
    }
}
