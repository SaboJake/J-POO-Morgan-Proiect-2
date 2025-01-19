package org.poo.exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(final String message) {
        super(message);
    }
}
