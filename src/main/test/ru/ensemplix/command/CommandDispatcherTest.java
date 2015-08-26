package ru.ensemplix.command;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CommandDispatcherTest {

    @Test
    public void testDispatcher() {
        SimpleCommand command = new SimpleCommand();
        SimpleSender sender = new SimpleSender();

        CommandDispatcher dispatcher = new CommandDispatcher();
        dispatcher.register(command, "test", "test2");

        assertTrue(dispatcher.call(sender, "/test nope"));
        assertTrue(dispatcher.call(sender, "/test2 hello"));
        assertTrue(command.hello && command.test);
    }

}
