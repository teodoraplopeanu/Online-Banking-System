package org.poo.main.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super();
    }

    public InsufficientFundsException(final String message) {
        super(message);
    }
}
