package org.poo.exceptions;

public class NoAccountException extends RuntimeException {
    public NoAccountException(final String message) {
        super(message);
    }
}
