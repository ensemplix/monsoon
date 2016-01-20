package ru.ensemplix.command;

import org.junit.Test;
import ru.ensemplix.command.region.SimpleRegion;
import ru.ensemplix.command.region.SimpleRegionCommand;
import ru.ensemplix.command.region.SimpleRegionParser;

import static org.junit.Assert.*;

public class CommandDispatcherTest {

    private CommandDispatcher dispatcher = new CommandDispatcher();

    @Test
    public void testDispatcher() throws CommandNotFoundException {
        SimpleCommand command = new SimpleCommand();
        SimpleRegionCommand region = new SimpleRegionCommand();
        SimpleSender sender = new SimpleSender();

        dispatcher.bind(SimpleRegion.class, new SimpleRegionParser());

        dispatcher.register(command, "test", "test2");
        dispatcher.register(region, "region", "rg");

        assertTrue(dispatcher.call(sender, "/test"));
        assertTrue(dispatcher.call(sender, "/test2"));
        assertFalse(dispatcher.call(sender, "/test hello"));
        assertTrue(dispatcher.call(sender, "/test2 integer 36"));
        assertFalse(dispatcher.call(sender, "/test integer"));
        assertTrue(dispatcher.call(sender, "/test2 string koala"));
        assertTrue(dispatcher.call(sender, "/test collection i love ensemplix <3"));
        assertTrue(dispatcher.call(sender, "/rg Project:Id"));

        assertTrue(command.hello && command.test);
        assertEquals(36, command.integer);
        assertEquals("koala", command.string);
        assertEquals("Project:Id", region.name);

        assertArrayEquals(new String[] {"i", "love", "ensemplix", "<3"}, command.strings.toArray());
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCommandNotFound() throws Exception {
        dispatcher.call(new SimpleSender(), "not existing command");
    }

}
