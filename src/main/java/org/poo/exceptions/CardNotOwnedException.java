package org.poo.exceptions;

public class CardNotOwnedException extends RuntimeException {
    public CardNotOwnedException(final String message) {
        super(message);
    }
}
