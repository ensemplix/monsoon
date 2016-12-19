package ru.ensemplix.command.argument

import ru.ensemplix.command.CommandContext
import ru.ensemplix.command.argument.Argument.Result.FAIL
import ru.ensemplix.command.argument.Argument.Result.SUCCESS
import ru.ensemplix.command.util.Sentence

class StringArgumentParser : ArgumentParser<String> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<String> {
        if (value == null || value.isEmpty()) {
            return Argument(FAIL, null)
        }

        return Argument(SUCCESS, value)
    }
}

class IntegerArgumentParser : ArgumentParser<Int> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Int> {
        try {
            return Argument(SUCCESS, Integer.parseInt(value))
        } catch (e: NumberFormatException) {
            return Argument(FAIL, 0)
        }
    }
}

class LongArgumentParser : ArgumentParser<Long> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Long> {
        try {
            return Argument(SUCCESS, java.lang.Long.parseLong(value))
        } catch (e: NumberFormatException) {
            return Argument(FAIL, 0)
        }
    }
}

class BooleanArgumentParser : ArgumentParser<Boolean> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Boolean> {
        if (value == null || value.isEmpty()) {
            return Argument(FAIL, false)
        }

        return Argument(SUCCESS, java.lang.Boolean.parseBoolean(value))
    }
}

class FloatArgumentParser : ArgumentParser<Float> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Float> {
        try {
            return Argument(SUCCESS, java.lang.Float.parseFloat(value))
        } catch (e: NumberFormatException) {
            return Argument(FAIL, 0f)
        }
    }
}

class DoubleArgumentParser : ArgumentParser<Double> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Double> {
        try {
            return Argument(SUCCESS, java.lang.Double.parseDouble(value))
        } catch (e: NumberFormatException) {
            return Argument(FAIL, 0.0)
        }
    }
}

class EnumArgumentParser(val enumType: Class<out Enum<*>>) : ArgumentParser<Enum<*>> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Enum<*>> {
        val argument = integerParser.parseArgument(context, index, value)
        val enumConstants = enumType.enumConstants

        if(argument.result == SUCCESS) {
            val enumIndex = argument.value as Int

            if(enumIndex <= enumConstants.size) {
                return Argument(SUCCESS, enumConstants[enumIndex])
            }
        }

        for(e in enumConstants) {
            if(e.name.equals(value, true)) {
                return Argument(SUCCESS, e)
            }
        }

        return Argument(FAIL, null)
    }

    companion object {
        private val integerParser = IntegerArgumentParser()
    }
}

class SentenceArgumentParser : ArgumentParser<Sentence> {
    override fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<Sentence> {
        val maxIndex = context.args.size

        if(maxIndex <= index) {
            return Argument(FAIL, null)
        }

        val slice = context.args.copyOfRange(index, maxIndex)
        val text = slice.joinToString(" ")

        return Argument(SUCCESS, Sentence(text))
    }

}
