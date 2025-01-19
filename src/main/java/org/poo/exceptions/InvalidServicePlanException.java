package org.poo.exceptions;

public class InvalidServicePlanException extends RuntimeException {
    public InvalidServicePlanException(final String message) {
        super(message);
    }
}
