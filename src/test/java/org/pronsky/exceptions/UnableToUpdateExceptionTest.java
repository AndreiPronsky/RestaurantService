package org.pronsky.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnableToUpdateExceptionTest {
    @Test
    void testDefaultConstructor() {
        UnableToUpdateException exception = new UnableToUpdateException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test message";
        UnableToUpdateException exception = new UnableToUpdateException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception cause = new Exception("Cause exception");
        UnableToUpdateException exception = new UnableToUpdateException(cause);
        assertEquals("java.lang.Exception: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause exception");
        UnableToUpdateException exception = new UnableToUpdateException(message, cause);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause exception");
        UnableToUpdateException exception = new UnableToUpdateException(cause);
        assertEquals("java.lang.RuntimeException: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        String message = "Test exception";
        assertThrows(UnableToUpdateException.class, () -> {
            throw new UnableToUpdateException(message);
        });
    }
}
