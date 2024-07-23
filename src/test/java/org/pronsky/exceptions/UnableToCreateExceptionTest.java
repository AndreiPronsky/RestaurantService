package org.pronsky.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnableToCreateExceptionTest {
    @Test
    void testDefaultConstructor() {
        UnableToCreateException exception = new UnableToCreateException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test message";
        UnableToCreateException exception = new UnableToCreateException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception cause = new Exception("Cause exception");
        UnableToCreateException exception = new UnableToCreateException(cause);
        assertEquals("java.lang.Exception: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause exception");
        UnableToCreateException exception = new UnableToCreateException(message, cause);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause exception");
        UnableToCreateException exception = new UnableToCreateException(cause);
        assertEquals("java.lang.RuntimeException: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        String message = "Test exception";
        assertThrows(UnableToCreateException.class, () -> {
            throw new UnableToCreateException(message);
        });
    }
}
