package org.poo.exceptions;

public class NoClassicAccountException extends RuntimeException {
    public NoClassicAccountException(final String message) {
        super(message);
    }
}
