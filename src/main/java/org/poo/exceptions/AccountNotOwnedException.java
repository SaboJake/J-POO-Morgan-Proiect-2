package org.poo.exceptions;

public class AccountNotOwnedException extends NoOutputNecessaryException {
    public AccountNotOwnedException(final String message) {
        super(message);
    }
}
