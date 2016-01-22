package ru.ensemplix.command;

import org.junit.Test;
import ru.ensemplix.command.region.Region;
import ru.ensemplix.command.region.RegionCommand;
import ru.ensemplix.command.region.RegionCompleter;
import ru.ensemplix.command.region.RegionParser;
import ru.ensemplix.command.simple.SimpleCommand;
import ru.ensemplix.command.simple.SimpleSender;

import static org.junit.Assert.*;

public class CommandDispatcherTest {

    private CommandDispatcher dispatcher = new CommandDispatcher();
    private CommandSender sender = new SimpleSender();

    @Test
    public void testDispatcher() throws CommandException {
        SimpleCommand command = new SimpleCommand();
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

    @Test
    public void testTypeParser() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.register(region, "parser");

        assertTrue(dispatcher.call(sender, "/parser Project:Id"));
        assertEquals("Project:Id", region.name);
    }

    @Test
    public void testTypeParserIterable() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.register(region, "iterable");

        assertTrue(dispatcher.call(sender, "/iterable list home spawn koala"));
        assertEquals(3, region.list.size());
    }

    @Test
    public void testCompleteObject() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.bind(Region.class, new RegionCompleter());
        dispatcher.register(region, "object2");

        String[] regions = dispatcher.complete(sender, "/object2").toArray(new String[4]);

        assertEquals("home", regions[0]);
        assertEquals("spawn", regions[1]);
        assertEquals("spawn123", regions[2]);
        assertEquals("spb", regions[3]);
    }

    @Test
    public void testCompleteObjectPartial() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.bind(Region.class, new RegionCompleter());
        dispatcher.register(region, "object");

        String[] regions = dispatcher.complete(sender, "/object sp").toArray(new String[3]);

        assertEquals("spawn", regions[0]);
        assertEquals("spawn123", regions[1]);
        assertEquals("spb", regions[2]);
    }

    @Test
    public void testCompleteIterable() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.bind(Region.class, new RegionCompleter());
        dispatcher.register(region, "iterable2");

        String[] regions = dispatcher.complete(sender, "/iterable2 list").toArray(new String[4]);

        assertEquals("home", regions[0]);
        assertEquals("spawn", regions[1]);
        assertEquals("spawn123", regions[2]);
        assertEquals("spb", regions[3]);
    }

    @Test
    public void testCompleteIterablePartial() throws CommandException {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionParser());
        dispatcher.bind(Region.class, new RegionCompleter());
        dispatcher.register(region, "iterable3");

        String[] regions = dispatcher.complete(sender, "/iterable3 list sp").toArray(new String[3]);

        assertEquals("spawn", regions[0]);
        assertEquals("spawn123", regions[1]);
        assertEquals("spb", regions[2]);
    }

    @Test
    public void testCompleteActions() throws CommandException {
        dispatcher.register(new Actions(), "actions");

        String[] actions = dispatcher.complete(sender, "/actions").toArray(new String[4]);

        assertEquals("add", actions[0]);
        assertEquals("addMember", actions[1]);
        assertEquals("view", actions[2]);
        assertEquals("list", actions[3]);
    }

    @Test
    public void testCompleteActionsPartial() throws CommandException {
        dispatcher.register(new Actions(), "actions2");

        String[] actions = dispatcher.complete(sender, "/actions2 ad").toArray(new String[2]);
        assertEquals("add", actions[0]);
        assertEquals("addMember", actions[1]);
    }

    @Test
    public void testRegisterNoObject() {
        registerWithException(null, "Please provide command object");
    }

    @Test
    public void testRegisterNameNull() {
        registerWithException(new Object(), "Please provide valid command name", null);
    }

    @Test
    public void testRegisterNameEmpty() {
        registerWithException(new Object(), "Please provide valid command name", "");
    }

    @Test
    public void testRegisterNameWhitespace() {
        registerWithException(new Object(), "Please provide command name with no whitespace", "test test");
    }

    @Test
    public void testRegisterAlreadyExists() {
        CommandDispatcher.commands.put("exists", null);
        registerWithException(new Object(), "Command with name exists already exists", "exists");
    }

    @Test
    public void testRegisterNoAnnotations() {
        registerWithException(new Object(), "Not found any method marked with @Command");
    }

    @Test
    public void testRegisterInvalidReturn() {
        registerWithException(new InvalidReturn(), "invalidReturn must return void or boolean");
    }

    @Test
    public void testRegisterNoSender() {
        registerWithException(new NoSender(), "Please provide command sender for noSender");
    }

    @Test
    public void testRegisterNoTypeParser() {
        registerWithException(new NoTypeParser(), "Please provide type parser for class java.lang.Object");
    }

    @Test
    public void testRegisterIterableLastParameter() {
        registerWithException(new IterableLastParameter(), "Iterable must be last parameter in iterableLastParameter");
    }

    @Test
    public void testCallSenderNull() {
        callWithException(null, "Please provide command sender", null);
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCallCmdNull() throws CommandException {
        dispatcher.call(sender, null);
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCallCmdEmpty() throws CommandException {
        dispatcher.call(sender, "");
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCallCmdEmptyPrefixed() throws CommandException {
        dispatcher.call(sender, "/");
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCallCommandNotFound() throws CommandException {
        dispatcher.call(sender, "not existing command");
    }

    @Test(expected = CommandNotFoundException.class)
    public void testCallCommandNoMain() throws CommandException {
        dispatcher.register(new NoMain(), "no_main");
        dispatcher.call(sender, "/no_main");
    }

    @Test(expected = CommandAccessException.class)
    public void testCallCommandNoAccess() throws CommandException {
        dispatcher.register(new Access(), "access");
        dispatcher.call(sender, "/access");
    }

    @Test
    public void testCallMain() throws CommandException {
        Main main = new Main();

        dispatcher.register(main, "main");
        dispatcher.call(sender, "/main bro");

        assertTrue(main.main);
    }

    @Test(expected = RuntimeException.class)
    public void testCallPropagateException() throws CommandException {
        dispatcher.register(new PropagateException(), "exception");
        dispatcher.call(sender, "/exception");
    }

    public void registerWithException(Object object, String message) {
        registerWithException(object, message, "name");
    }

    public void registerWithException(Object object, String message, String name) {
        try {
            dispatcher.register(object, name);
            fail();
        } catch(Exception e) {
            if(!message.equals(e.getMessage())) {
                throw new AssertionError(e);
            }
        }
    }

    public void callWithException(CommandSender sender, String message, String cmd) {
        try {
            dispatcher.call(sender, cmd);
            fail();
        } catch(Exception e) {
            if(!message.equals(e.getMessage())) {
                throw new AssertionError(e);
            }
        }
    }

    public class InvalidReturn {
        @Command
        public Object invalidReturn() {
            return new Object();
        }
    }

    public class NoSender {
        @Command
        public boolean noSender() {
            return true;
        }
    }

    public class NoTypeParser {
        @Command
        public boolean noTypeParser(CommandSender sender, Object obj) {
            return true;
        }
    }

    public class IterableLastParameter {
        @Command
        public boolean iterableLastParameter(CommandSender sender, Iterable<String> iterable, int value) {
            return true;
        }
    }

    public class NoMain {
        @Command
        public void nomain(CommandSender sender) {
            fail();
        }
    }

    public class Main {
        public boolean main;

        @Command(main = true)
        public void expectedCall(CommandSender sender) {
            main = true;
        }
    }

    public class Access {
        @Command(main = true)
        public void access(CommandSender sender) {

        }
    }

    public class PropagateException {
        @Command(main = true)
        public void exception(CommandSender sender) {
            throw new RuntimeException();
        }
    }

    public class Actions {
        @Command
        public void list(CommandSender sender) {

        }

        @Command
        public void view(CommandSender sender) {

        }

        @Command
        public void add(CommandSender sender) {

        }

        @Command
        public void addMember(CommandSender sender) {

        }
    }

}
