package ru.ensemplix.command;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class CommandTest {

    @Test
    public void testDefaultMain() throws NoSuchMethodException {
        assertFalse((Boolean) Command.class.getMethod("main").getDefaultValue());
    }

}
