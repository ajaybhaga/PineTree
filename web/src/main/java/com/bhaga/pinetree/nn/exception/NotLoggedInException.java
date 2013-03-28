package com.bhaga.pinetree.nn.exception;

/**
 * Not Logged In Exception
 *
 * @author Ajay Bhaga
 */
public class NotLoggedInException extends Exception {

    private String message = null;

    public NotLoggedInException() {
        super();
    }

    public NotLoggedInException(String message) {
        super(message);
        this.initCause(new Throwable(message));
        this.message = message;
    }

    public NotLoggedInException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}