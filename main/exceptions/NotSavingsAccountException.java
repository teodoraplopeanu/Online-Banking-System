package org.poo.main.exceptions;

public class NotSavingsAccountException extends Exception {
    public NotSavingsAccountException() {
        super();
    }

    public NotSavingsAccountException(final String message) {
        super(message);
    }
}
