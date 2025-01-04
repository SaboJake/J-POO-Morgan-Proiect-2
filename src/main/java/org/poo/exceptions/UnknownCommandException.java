package org.poo.exceptions;

public class UnknownCommandException extends NoOutputNecessaryException {
    public UnknownCommandException(final String message) {
        super(message);
    }
}
