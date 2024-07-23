package org.pronsky.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnableToDeleteExceptionTest {
    @Test
    void testDefaultConstructor() {
        UnableToDeleteException exception = new UnableToDeleteException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test message";
        UnableToDeleteException exception = new UnableToDeleteException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception cause = new Exception("Cause exception");
        UnableToDeleteException exception = new UnableToDeleteException(cause);
        assertEquals("java.lang.Exception: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause exception");
        UnableToDeleteException exception = new UnableToDeleteException(message, cause);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause exception");
        UnableToDeleteException exception = new UnableToDeleteException(cause);
        assertEquals("java.lang.RuntimeException: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        String message = "Test exception";
        assertThrows(UnableToDeleteException.class, () -> {
            throw new UnableToDeleteException(message);
        });
    }
}
