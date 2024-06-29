package org.pronsky.data.exceptions;

import org.pronsky.ApplicationException;

public class UnableToDeleteException extends ApplicationException {
    public UnableToDeleteException() {
        super();
    }

    public UnableToDeleteException(String message) {
        super(message);
    }

    public UnableToDeleteException(Exception e) {
        super(e);
    }

    public UnableToDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToDeleteException(Throwable cause) {
        super(cause);
    }
}
