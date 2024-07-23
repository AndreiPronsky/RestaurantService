package org.pronsky.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class PropertyReaderTest {

    @BeforeEach
    void setUp() {
        PropertyReader.instance = PropertyReader.getInstance();
    }

    @Test
    void testGetInstance_Singleton() {
        PropertyReader instance1 = PropertyReader.getInstance();
        PropertyReader instance2 = PropertyReader.getInstance();

        assertSame(instance1, instance2);
    }
}
