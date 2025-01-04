package org.poo.exceptions;

public class NotSavingsAccountException extends RuntimeException {
    public NotSavingsAccountException(final String message) {
        super(message);
    }
}
