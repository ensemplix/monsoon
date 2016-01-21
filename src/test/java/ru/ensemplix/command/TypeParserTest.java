package ru.ensemplix.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.ensemplix.command.TypeParser.*;

public class TypeParserTest {

    @Test
    public void testStringParser() {
        StringParser parser = new StringParser();

        assertEquals("string 12", parser.parse("string 12"));
    }

    @Test
    public void testIntegerParser() {
        IntegerParser parser = new IntegerParser();

        assertEquals(12, (int) parser.parse("12"));
        assertEquals(0, (int) parser.parse("string"));
    }

    @Test
    public void testBooleanParser() {
        BooleanParser parser = new BooleanParser();

        assertTrue(parser.parse("true"));
        assertFalse(parser.parse("false"));
        assertFalse(parser.parse("string"));
    }

    @Test
    public void testFloatParser() {
        FloatParser parser = new FloatParser();

        assertEquals(1.6F, parser.parse("1.6"), 0);
        assertEquals(0F, parser.parse("string"), 0);
    }

    @Test
    public void testDoubleParser() {
        DoubleParser parser = new DoubleParser();

        assertEquals(1.6D, parser.parse("1.6"), 0);
        assertEquals(0D, parser.parse("string"), 0);
    }

}
