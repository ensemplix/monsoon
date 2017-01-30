package ru.ensemplix.command.dispatcher;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.ensemplix.command.Command;
import ru.ensemplix.command.CommandContext;
import ru.ensemplix.command.CommandResult;
import ru.ensemplix.command.CommandSender;
import ru.ensemplix.command.argument.Argument;
import ru.ensemplix.command.argument.EnumArgumentParser;
import ru.ensemplix.command.region.Region;
import ru.ensemplix.command.region.RegionArgumentParser;
import ru.ensemplix.command.region.RegionCommand;
import ru.ensemplix.command.region.RegionCompleter;
import ru.ensemplix.command.simple.SimpleCommand;
import ru.ensemplix.command.simple.SimpleSender;

import java.util.List;

import static org.junit.Assert.*;
import static ru.ensemplix.command.simple.SimpleCommand.SimpleEnum;
import static ru.ensemplix.command.simple.SimpleCommand.SimpleEnum.WORLD;

public class SimpleCommandDispatcherTest {

    @Rule
    public final ExpectedException expected = ExpectedException.none();

    private final SimpleCommandDispatcher dispatcher = new SimpleCommandDispatcher();
    private final CommandSender sender = new SimpleSender();

    @Before
    public void setUp() {
        dispatcher.bind(SimpleEnum.class, new EnumArgumentParser(SimpleEnum.class));
        dispatcher.bind(Region.class, new RegionArgumentParser());
        dispatcher.bind(Region.class, new RegionCompleter());
    }

    @Test
    public void testCommandRegister() {
        SimpleCommand command = new SimpleCommand();

        dispatcher.register(command, "register");
        assertTrue(SimpleCommandDispatcher.commands.containsKey("register"));
    }

    @Test
    public void testCommandUnregister() {
        SimpleCommand command = new SimpleCommand();

        dispatcher.register(command, "unregister");
        dispatcher.unregister(SimpleCommand.class);

        assertFalse(SimpleCommandDispatcher.commands.containsKey("unregister"));
    }

    @Test
    public void testDispatcher() {
        SimpleCommand command = new SimpleCommand();
        dispatcher.register(command, "test", "test2");

        assertTrue(call("test"));
        assertTrue(call("test2"));
        assertFalse(call("test hello"));
        assertTrue(call("test2 integer 36"));
        assertFalse(call("test integer"));
        assertTrue(call("test2 string koala"));
        assertTrue(call("test collection i love ensemplix <3"));
        assertTrue(call("test2 argument koala"));
        assertTrue(call("test2 argument2"));
        assertTrue(call("test2 enumm world"));
        assertTrue(call("test2 longg 12"));
        assertTrue(call("test sentence i love ensemplix <3"));

        assertTrue(command.hello && command.test);
        assertEquals(36, command.integer);
        assertEquals("koala", command.string);
        assertEquals(Argument.Result.SUCCESS, command.argument.getResult());
        assertEquals("koala", command.argument.getValue());
        assertEquals(Argument.Result.FAIL, command.argument2.getResult());
        assertNull(command.argument2.getValue());
        assertEquals(WORLD, command.enumm);
        assertEquals(12, command.longg);

        assertEquals("i love ensemplix <3", command.sentence.getText());
        assertArrayEquals(new String[]{"i", "love", "ensemplix", "<3"}, command.strings.toArray());
    }

    @Test
    public void testActionCallAlias() {
        ActionAlias command = new ActionAlias();
        dispatcher.register(command, "alias");
        call("alias simple");

        assertTrue(command.called);
    }

    @Test
    public void testActionCallWrongAlias() {
        ActionAlias command = new ActionAlias();
        dispatcher.register(command, "alias2");

        validate("alias2 wrong");
    }

    @Test
    public void testActionCallAsCommand() {
        ActionAsCommand command = new ActionAsCommand();
        dispatcher.register(command, "origin");
        call("asCommand redirect");

        assertTrue(command.called);
    }

    @Test
    public void testCommandCallCaseSensitive() {
        dispatcher.register(new SimpleCommand(), "test3");
        call("teSt3");
    }

    @Test
    public void testCommandRegisterCaseSensitive() {
        dispatcher.register(new SimpleCommand(), "teSt4");
        call("test4");
    }

    @Test
    public void testActionCallCaseSensitive() {
        dispatcher.register(new CaseSensivity(), "case");
        call("case listall");
    }

    @Test
    public void testActionRegisterCaseSensitive() {
        dispatcher.register(new CaseSensivity(), "case2");
        call("case2 liStaLl");
    }

    @Test
    public void testCommandResult() {
        SimpleCommand command = new SimpleCommand();
        dispatcher.register(command, "test", "test2");

        CommandContext context = dispatcher.validate(sender, "test2 integer 36");
        CommandResult result = dispatcher.call(sender, context);
        List<Argument<?>> arguments = result.getArguments();

        assertTrue(result.isSuccess());
        assertEquals("test", context.getCommandName());
        assertEquals("integer", context.getActionName());
        assertTrue(arguments.size() == 1);
        assertEquals(Argument.Result.SUCCESS, arguments.get(0).getResult());
        assertEquals(36, arguments.get(0).getValue());
        assertEquals("36", arguments.get(0).getText());
    }

    @Test
    public void testTypeParser() {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionArgumentParser());
        dispatcher.register(region, "parser");

        assertTrue(call("parser Project:Id"));
        assertEquals("project:id", region.name);
    }

    @Test
    public void testCompleteObjectPartial() {
        RegionCommand region = new RegionCommand();
        dispatcher.register(region, "object");

        String[] regions = dispatcher.complete(sender, "object sp").toArray(new String[3]);

        assertEquals("spawn", regions[0]);
        assertEquals("spawn123", regions[1]);
        assertEquals("spb", regions[2]);
    }

    @Test
    public void testCompleteObjectWithArgument() {
        RegionCommand region = new RegionCommand();
        dispatcher.register(region, "object2");

        String[] regions = dispatcher.complete(sender, "object2 remove ").toArray(new String[4]);

        assertEquals("home", regions[0]);
        assertEquals("spawn", regions[1]);
        assertEquals("spawn123", regions[2]);
        assertEquals("spb", regions[3]);
    }

    @Test
    public void testTypeParserIterable() {
        RegionCommand region = new RegionCommand();

        dispatcher.bind(Region.class, new RegionArgumentParser());
        dispatcher.register(region, "iterable");

        assertTrue(call("iterable list home spawn koala"));

        List<Argument<Region>> regions = region.list;

        assertEquals(3, regions.size());
        assertEquals("home", regions.get(0).getValue().name);
        assertEquals("spawn", regions.get(1).getValue().name);
        assertEquals("koala", regions.get(2).getValue().name);
    }

    @Test
    public void testCompleteIterable() {
        RegionCommand region = new RegionCommand();
        dispatcher.register(region, "iterable2");

        String[] regions = dispatcher.complete(sender, "iterable2 list ").toArray(new String[4]);

        assertEquals("home", regions[0]);
        assertEquals("spawn", regions[1]);
        assertEquals("spawn123", regions[2]);
        assertEquals("spb", regions[3]);
    }

    @Test
    public void testCompleteIterablePartial() {
        RegionCommand region = new RegionCommand();
        dispatcher.register(region, "iterable3");

        String[] regions = dispatcher.complete(sender, "iterable3 list sp").toArray(new String[3]);

        assertEquals("spawn", regions[0]);
        assertEquals("spawn123", regions[1]);
        assertEquals("spb", regions[2]);
    }

    @Test
    public void testCompleteArgumentPartial() {
        dispatcher.register(new Actions(), "actions2");

        String[] actions = dispatcher.complete(sender, "actions2 ad").toArray(new String[2]);

        assertEquals("add", actions[0]);
        assertEquals("addmember", actions[1]);
    }

    @Test
    public void testCompleteAction() {
        dispatcher.register(new RegionCommand(), "actions3");

        assertTrue(dispatcher.complete(sender, "actions3 remove").isEmpty());
    }

    @Test
    public void testCompleteActionPartial() {
        dispatcher.register(new RegionCommand(), "actions4");

        String[] actions = dispatcher.complete(sender, "actions4 rem").toArray(new String[1]);

        assertEquals("remove", actions[0]);
    }

    @Test
    public void testCompleteCommand() {
        dispatcher.register(new Actions(), "actions5");

        assertTrue(dispatcher.complete(sender, "actions5").isEmpty());
    }

    @Test
    public void testCompleteNotRequired() {
        dispatcher.register(new RegionCommand(), "actions6");

        assertTrue(dispatcher.complete(sender, "actions6 remove home ").isEmpty());
    }

    @Test
    public void testCompleteCommandPartial() {
        dispatcher.register(new SimpleCommand(), "simple");
        dispatcher.register(new SimpleCommand(), "simple2");

        String[] commands = dispatcher.complete(sender, "sim").toArray(new String[2]);

        assertEquals("/simple", commands[0]);
        assertEquals("/simple2", commands[1]);
    }

    @Test
    public void testCompleteCommands() {
        dispatcher.register(new SimpleCommand(), "bcd");
        dispatcher.register(new SimpleCommand(), "abc");

        String[] commands = dispatcher.complete(sender, "").toArray(new String[2]);

        assertEquals("/bcd", commands[0]);
        assertEquals("/abc", commands[1]);
    }

    @Test
    public void testRegisterNameNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide valid command name");

        dispatcher.register(new Object(), "name", null);
    }

    @Test
    public void testRegisterNameEmpty() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide valid command name");

        dispatcher.register(new Object(), "");

    }

    @Test
    public void testRegisterNameWhitespace() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide command name with no whitespace");

        dispatcher.register(new Object(), "test test");
    }

    @Test
    public void testRegisterAlreadyExists() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Command with name exists already exists");

        dispatcher.register(new SimpleCommand(), "exists");
        dispatcher.register(new SimpleCommand(), "exists");
    }

    @Test
    public void testRegisterNoAnnotations() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage("Not found any method marked with @Command");

        dispatcher.register(new Object());
    }

    @Test
    public void testRegisterInvalidReturn() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("invalidReturn must return void or boolean");

        dispatcher.register(new InvalidReturn());
    }

    @Test
    public void testRegisterNoSender() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide command sender for noSender");

        dispatcher.register(new NoSender());
    }

    @Test
    public void testRegisterNoTypeParser() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide type parser for class java.lang.Object");

        dispatcher.register(new NoTypeParser());
    }

    @Test
    public void testRegisterNoTypeParser2() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Please provide type parser for class java.lang.Object");

        dispatcher.register(new NoTypeParser2());
    }

    @Test
    public void testRegisterIterableLastParameter() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Iterable must be last parameter in iterableLastParameter");

        dispatcher.register(new IterableLastParameter());
    }

    @Test
    public void testCallCmdEmpty() {
        validate("");
    }

    @Test
    public void testCallCmdEmptyPrefixed() {
        validate("");
    }

    @Test
    public void testCallCommandNotFound() {
        validate("not existing command");
    }

    @Test
    public void testCallCommandNoMain() {
        dispatcher.register(new NoMain(), "no_main");
        validate("no_main");
    }

    @Test
    public void testCallMain() {
        Main main = new Main();

        dispatcher.register(main, "main");
        call("main bro");

        assertTrue(main.main);
    }

    @Test(expected = RuntimeException.class)
    public void testCallPropagateException() {
        dispatcher.register(new PropagateException(), "exception");
        call("exception");
    }

    @Test
    public void testCallNotMain() {
        dispatcher.register(new NotMain(), "notmain");
        call("notmain delete");
    }

    @Test
    public void testCallActionRedirect() {
        SameActionRedirect command = new SameActionRedirect();

        dispatcher.register(command, "redirect");
        call("redirect delete something");

        assertEquals("something", command.something);
    }

    @Test
    public void testCallMainActionRedirect() {
        SameMainActionRedirect command = new SameMainActionRedirect();

        dispatcher.register(command, "redirect2");
        call("redirect2 del");

        assertEquals("del", command.something);
    }

    private boolean validate(String cmd) {
        return dispatcher.validate(sender, cmd) != null;
    }

    private boolean call(String cmd) {
        CommandContext context = dispatcher.validate(sender, cmd);

        if(context != null) {
            return dispatcher.call(sender, context).isSuccess();
        }

        return false;
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

    public class NoTypeParser2 {
        @Command
        public boolean noTypeParser2(CommandSender sender, Argument<Object> argument) {
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
        private boolean main;

        @Command(main = true)
        public void expectedCall(CommandSender sender) {
            main = true;
        }
    }

    public class Access {
        @Command(main = true, permission = true)
        public void access(CommandSender sender) {

        }
    }

    public class Access2 {
        @Command(permission = true)
        public void test(CommandSender sender) {

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

    public class CaseSensivity {
        @Command
        public void listAll(CommandSender sender) {

        }
    }

    public class NotMain {
        @Command(main = true)
        public void set(SimpleSender player, String prefix) {
            fail();
        }

        @Command
        public void delete(SimpleSender sender, Argument<Region> argument) {

        }
    }

    public class SameActionRedirect {
        private String something;

        @Command
        public void delete(SimpleSender sender, String something) {
            this.something = something;
        }

        @Command
        public void delete(SimpleSender sender, String something, Argument<Region> argument) {
            fail();
        }
    }


    public class SameMainActionRedirect {
        private String something;

        @Command(main = true)
        public void delete(SimpleSender sender, String something) {
            this.something = something;
        }

        @Command(main = true)
        public void delete(SimpleSender sender, String something, Argument<Region> argument) {
            fail();
        }
    }

    public class ActionAlias {
        private boolean called;

        @Command(aliases = "simple")
        public void redirect(SimpleSender sender) {
            this.called = true;
        }
    }

    public class ActionAsCommand {
        private boolean called;

        @Command(asCommand = "asCommand")
        public void redirect(SimpleSender sender) {
            this.called = true;
        }
    }

}
