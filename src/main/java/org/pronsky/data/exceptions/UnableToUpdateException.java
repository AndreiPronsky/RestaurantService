package org.pronsky.data.exceptions;

import org.pronsky.ApplicationException;

public class UnableToUpdateException extends ApplicationException {
    public UnableToUpdateException() {
        super();
    }

    public UnableToUpdateException(String message) {
        super(message);
    }

    public UnableToUpdateException(Exception e) {
        super(e);
    }

    public UnableToUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToUpdateException(Throwable cause) {
        super(cause);
    }
}
