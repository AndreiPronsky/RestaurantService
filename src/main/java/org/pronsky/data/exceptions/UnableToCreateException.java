package org.pronsky.data.exceptions;

import org.pronsky.ApplicationException;

public class UnableToCreateException extends ApplicationException {
    public UnableToCreateException() {
        super();
    }

    public UnableToCreateException(String message) {
        super(message);
    }

    public UnableToCreateException(Exception e) {
        super(e);
    }

    public UnableToCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToCreateException(Throwable cause) {
        super(cause);
    }
}
