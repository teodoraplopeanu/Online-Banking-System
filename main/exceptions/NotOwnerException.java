package org.poo.main.exceptions;

public class NotOwnerException extends Exception {
    public NotOwnerException() {
        super();
    }

    public NotOwnerException(final String message) {
        super(message);
    }
}
