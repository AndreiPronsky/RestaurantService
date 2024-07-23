package org.pronsky.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionExceptionTest {
    @Test
    void testDefaultConstructor() {
        ConnectionException exception = new ConnectionException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test message";
        ConnectionException exception = new ConnectionException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception cause = new Exception("Cause exception");
        ConnectionException exception = new ConnectionException(cause);
        assertEquals("java.lang.Exception: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause exception");
        ConnectionException exception = new ConnectionException(message, cause);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause exception");
        ConnectionException exception = new ConnectionException(cause);
        assertEquals("java.lang.RuntimeException: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        String message = "Test exception";
        assertThrows(ConnectionException.class, () -> {
            throw new ConnectionException(message);
        });
    }
}
