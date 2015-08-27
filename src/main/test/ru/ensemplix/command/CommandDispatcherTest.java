package ru.ensemplix.command;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandDispatcherTest {

    @Test
    public void testDispatcher() throws CommandNotFoundException {
        SimpleCommand command = new SimpleCommand();
        SimpleSender sender = new SimpleSender();

        CommandDispatcher dispatcher = new CommandDispatcher();
        dispatcher.register(command, "test", "test2");

        assertTrue(dispatcher.call(sender, "/test"));
        assertTrue(dispatcher.call(sender, "/test2"));
        assertFalse(dispatcher.call(sender, "/test hello"));
        assertTrue(dispatcher.call(sender, "/test2 integer 36"));
        assertFalse(dispatcher.call(sender, "/test integer"));
        assertTrue(dispatcher.call(sender, "/test2 string koala"));
        assertTrue(dispatcher.call(sender, "/test collection i love ensemplix <3"));

        assertTrue(command.hello && command.test);
        assertEquals(36, command.integer);
        assertEquals("koala", command.string);
        assertArrayEquals(new String[] {"i", "love", "ensemplix", "<3"}, command.strings.toArray());
    }

}
