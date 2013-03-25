package com.bhaga.pinetree.nn.exception;

/**
 * No Data Exception
 *
 * @author Ajay Bhaga
 */
public class NoDataException extends Exception {

    private String message = null;

    public NoDataException() {
        super();
    }

    public NoDataException(String message) {
        super(message);
        this.initCause(new Throwable(message));
        this.message = message;
    }

    public NoDataException(Throwable cause) {
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