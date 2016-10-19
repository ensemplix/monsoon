package ru.ensemplix.command.argument;

import org.junit.Test;

import static org.junit.Assert.*;
import static ru.ensemplix.command.argument.Argument.*;
import static ru.ensemplix.command.argument.Argument.Result.FAIL;
import static ru.ensemplix.command.argument.Argument.Result.SUCCESS;
import static ru.ensemplix.command.argument.ArgumentParser.*;

public class ArgumentParserTest {

    @Test
    public void testStringParser() {
        StringArgumentParser parser = new StringArgumentParser();
        Argument<String> result = parser.parseArgument("string 12");
        Argument<String> result2 = parser.parseArgument("");

        assertEquals(SUCCESS, result.getResult());
        assertEquals("string 12", result.getValue());
        assertEquals(FAIL, result2.getResult());
        assertEquals(null, result2.getValue());
    }

    @Test
    public void testIntegerParser() {
        IntegerArgumentParser parser = new IntegerArgumentParser();
        Argument<Integer> result = parser.parseArgument("12");
        Argument<Integer> result2 = parser.parseArgument("string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(12, (int) result.getValue());
        assertEquals(FAIL, result2.getResult());
        assertEquals(0, (int) result2.getValue());
    }

    @Test
    public void testBooleanParser() {
        BooleanArgumentParser parser = new BooleanArgumentParser();
        Argument<Boolean> result = parser.parseArgument("true");
        Argument<Boolean> result2 = parser.parseArgument("false");
        Argument<Boolean> result3 = parser.parseArgument("string");
        Argument<Boolean> result4 = parser.parseArgument("");

        assertEquals(SUCCESS, result.getResult());
        assertTrue(result.getValue());
        assertEquals(SUCCESS, result2.getResult());
        assertFalse(result2.getValue());
        assertEquals(SUCCESS, result3.getResult());
        assertFalse(result3.getValue());
        assertEquals(FAIL, result4.getResult());
        assertFalse(result4.getValue());
    }

    @Test
    public void testFloatParser() {
        FloatArgumentParser parser = new FloatArgumentParser();
        Argument<Float> result = parser.parseArgument("1.6");
        Argument<Float> result2 = parser.parseArgument("string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(1.6F, result.getValue(), 0);
        assertEquals(FAIL, result2.getResult());
        assertEquals(0F, result2.getValue(), 0);
    }

    @Test
    public void testDoubleParser() {
        DoubleArgumentParser parser = new DoubleArgumentParser();
        Argument<Double> result = parser.parseArgument("1.6");
        Argument<Double> result2 = parser.parseArgument("string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(1.6D, result.getValue(), 0);
        assertEquals(FAIL, result2.getResult());
        assertEquals(0F, result2.getValue(), 0);
    }

    @Test
    public void testEnumParser() {
        EnumArgumentParser parser = new EnumArgumentParser(Result.class);
        Argument<Enum<?>> result = parser.parseArgument("success");
        Argument<Enum<?>> result2 = parser.parseArgument("0");
        Argument<Enum<?>> result3 = parser.parseArgument("string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(SUCCESS, result.getValue());
        assertEquals(SUCCESS, result2.getResult());
        assertEquals(SUCCESS, result2.getResult());
        assertEquals(FAIL, result3.getResult());
        assertEquals(FAIL, result3.getResult());
    }

}
