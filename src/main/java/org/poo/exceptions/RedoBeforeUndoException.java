package org.poo.exceptions;

public class RedoBeforeUndoException extends RuntimeException {
    public RedoBeforeUndoException(final String message) {
        super(message);
    }
}
