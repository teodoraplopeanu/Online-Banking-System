package org.poo.main.exceptions;

public class CardNotFoundException extends Exception {
    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(final String message) {
        super(message);
    }
}
