package ru.ensemplix.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.ensemplix.command.CommandArgumentParser.*;

public class CommandArgumentParserTest {

    @Test
    public void testStringParser() {
        StringArgumentParser parser = new StringArgumentParser();

        assertEquals("string 12", parser.parseArgument("string 12"));
    }

    @Test
    public void testIntegerParser() {
        IntegerArgumentParser parser = new IntegerArgumentParser();

        assertEquals(12, (int) parser.parseArgument("12"));
        assertEquals(0, (int) parser.parseArgument("string"));
    }

    @Test
    public void testBooleanParser() {
        BooleanArgumentParser parser = new BooleanArgumentParser();

        assertTrue(parser.parseArgument("true"));
        assertFalse(parser.parseArgument("false"));
        assertFalse(parser.parseArgument("string"));
    }

    @Test
    public void testFloatParser() {
        FloatArgumentParser parser = new FloatArgumentParser();

        assertEquals(1.6F, parser.parseArgument("1.6"), 0);
        assertEquals(0F, parser.parseArgument("string"), 0);
    }

    @Test
    public void testDoubleParser() {
        DoubleArgumentParser parser = new DoubleArgumentParser();

        assertEquals(1.6D, parser.parseArgument("1.6"), 0);
        assertEquals(0D, parser.parseArgument("string"), 0);
    }

}
