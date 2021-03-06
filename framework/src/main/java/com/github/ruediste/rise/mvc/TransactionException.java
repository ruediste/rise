package com.github.ruediste.rise.mvc;

/**
 * Wrapper exception used for exceptions related to transactions
 */
public class TransactionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TransactionException() {
        super();
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

}
