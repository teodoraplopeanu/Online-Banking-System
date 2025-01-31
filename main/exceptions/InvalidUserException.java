package org.poo.main.exceptions;

public class InvalidUserException extends Exception {
    public InvalidUserException() {
        super();
    }

    public InvalidUserException(final String message) {
        super(message);
    }
}
