package org.poo.main.exceptions;

public class FrozenCardException extends Exception {
    public FrozenCardException() {
        super();
    }

    public FrozenCardException(final String message) {
        super(message);
    }
}
