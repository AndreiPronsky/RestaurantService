package org.pronsky.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionTest {

    @Test
    void testDefaultConstructor() {
        ApplicationException exception = new ApplicationException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test message";
        ApplicationException exception = new ApplicationException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception cause = new Exception("Cause exception");
        ApplicationException exception = new ApplicationException(cause);
        assertEquals("java.lang.Exception: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause exception");
        ApplicationException exception = new ApplicationException(message, cause);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause exception");
        ApplicationException exception = new ApplicationException(cause);
        assertEquals("java.lang.RuntimeException: Cause exception", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExceptionThrown() {
        String message = "Test exception";
        assertThrows(ApplicationException.class, () -> {
            throw new ApplicationException(message);
        });
    }
}
