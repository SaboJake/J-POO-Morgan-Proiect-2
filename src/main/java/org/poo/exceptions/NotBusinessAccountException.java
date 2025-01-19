package org.poo.exceptions;

public class NotBusinessAccountException extends RuntimeException {
    public NotBusinessAccountException(final String message) {
        super(message);
    }
}
