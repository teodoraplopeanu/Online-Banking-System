package org.poo.main.exceptions;

public class NoDowngradeException extends Exception {
    public NoDowngradeException() {
        super();
    }

    public NoDowngradeException(final String message) {
        super(message);
    }
}
