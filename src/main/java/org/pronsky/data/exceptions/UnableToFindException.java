package org.pronsky.data.exceptions;

import org.pronsky.ApplicationException;

public class UnableToFindException extends ApplicationException {
    public UnableToFindException() {
        super();
    }

    public UnableToFindException(String message) {
        super(message);
    }

    public UnableToFindException(Exception e) {
        super(e);
    }

    public UnableToFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToFindException(Throwable cause) {
        super(cause);
    }
}
