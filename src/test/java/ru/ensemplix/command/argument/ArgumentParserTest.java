package ru.ensemplix.command.argument;

import org.junit.Test;
import ru.ensemplix.command.CommandContext;
import ru.ensemplix.command.CommandHandler;
import ru.ensemplix.command.util.Sentence;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.*;
import static ru.ensemplix.command.argument.Argument.Result;
import static ru.ensemplix.command.argument.Argument.Result.FAIL;
import static ru.ensemplix.command.argument.Argument.Result.SUCCESS;

public class ArgumentParserTest {

    private final CommandHandler HANDLER_STUB = new CommandHandler("stub", this, emptyList(), emptyMap());
    private final CommandContext CONTEXT_STUB = new CommandContext("stub", null, null, new String[0], HANDLER_STUB);

    @Test
    public void testStringParser() {
        StringArgumentParser parser = new StringArgumentParser();
        Argument<String> result = parser.parseArgument(CONTEXT_STUB, 0, "string 12");
        Argument<String> result2 = parser.parseArgument(CONTEXT_STUB, 0, "");

        assertEquals(SUCCESS, result.getResult());
        assertEquals("string 12", result.getValue());
        assertEquals(FAIL, result2.getResult());
        assertEquals(null, result2.getValue());
    }

    @Test
    public void testIntegerParser() {
        IntegerArgumentParser parser = new IntegerArgumentParser();
        Argument<Integer> result = parser.parseArgument(CONTEXT_STUB, 0, "12");
        Argument<Integer> result2 = parser.parseArgument(CONTEXT_STUB, 0, "string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(12, (int) result.getValue());
        assertEquals(FAIL, result2.getResult());
        assertEquals(0, (int) result2.getValue());
    }

    @Test
    public void testLongParser() {
        LongArgumentParser parser = new LongArgumentParser();
        Argument<Long> result = parser.parseArgument(CONTEXT_STUB, 0, "12");
        Argument<Long> result2 = parser.parseArgument(CONTEXT_STUB, 0, "string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(12, (long) result.getValue());
        assertEquals(FAIL, result2.getResult());
        assertEquals(0, (long) result2.getValue());
    }

    @Test
    public void testBooleanParser() {
        BooleanArgumentParser parser = new BooleanArgumentParser();
        Argument<Boolean> result = parser.parseArgument(CONTEXT_STUB, 0, "true");
        Argument<Boolean> result2 = parser.parseArgument(CONTEXT_STUB, 0, "false");
        Argument<Boolean> result3 = parser.parseArgument(CONTEXT_STUB, 0, "string");
        Argument<Boolean> result4 = parser.parseArgument(CONTEXT_STUB, 0, "");

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
        Argument<Float> result = parser.parseArgument(CONTEXT_STUB, 0, "1.6");
        Argument<Float> result2 = parser.parseArgument(CONTEXT_STUB, 0, "string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(1.6F, result.getValue(), 0);
        assertEquals(FAIL, result2.getResult());
        assertEquals(0F, result2.getValue(), 0);
    }

    @Test
    public void testDoubleParser() {
        DoubleArgumentParser parser = new DoubleArgumentParser();
        Argument<Double> result = parser.parseArgument(CONTEXT_STUB, 0, "1.6");
        Argument<Double> result2 = parser.parseArgument(CONTEXT_STUB, 0, "string");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(1.6D, result.getValue(), 0);
        assertEquals(FAIL, result2.getResult());
        assertEquals(0F, result2.getValue(), 0);
    }

    @Test
    public void testEnumParser() {
        EnumArgumentParser parser = new EnumArgumentParser(Result.class);
        Argument<Enum<?>> result = parser.parseArgument(CONTEXT_STUB, 0, "success");
        Argument<Enum<?>> result2 = parser.parseArgument(CONTEXT_STUB, 0, "0");
        Argument<Enum<?>> result3 = parser.parseArgument(CONTEXT_STUB, 0, "string");
        Argument<Enum<?>> result4 = parser.parseArgument(CONTEXT_STUB, 0, "3");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(SUCCESS, result.getValue());
        assertEquals(SUCCESS, result2.getResult());
        assertEquals(SUCCESS, result2.getResult());
        assertEquals(FAIL, result3.getResult());
        assertEquals(FAIL, result3.getResult());
        assertEquals(FAIL, result4.getResult());
        assertEquals(FAIL, result4.getResult());
    }

    @Test
    public void tesetSentenceParser() {
        String text = "I am your sentence";

        SentenceArgumentParser parser = new SentenceArgumentParser();
        CommandContext context = new CommandContext("stub", null, null, text.split(" "), HANDLER_STUB);

        Argument<Sentence> result = parser.parseArgument(context, 0, "i");
        Argument<Sentence> result2 = parser.parseArgument(context, 1, "am");
        Argument<Sentence> result3 = parser.parseArgument(context, 5, "");

        assertEquals(SUCCESS, result.getResult());
        assertEquals(text, result.getValue().getText());
        assertEquals(4, result.getConsume());
        assertEquals(SUCCESS, result2.getResult());
        assertEquals("am your sentence", result2.getValue().getText());
        assertEquals(3, result2.getConsume());
        assertEquals(FAIL, result3.getResult());
    }

}
