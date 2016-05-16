package ru.ensemplix.command.argument;

import static ru.ensemplix.command.argument.Argument.Result.FAIL;
import static ru.ensemplix.command.argument.Argument.Result.SUCCESS;

/**
 * Реализация данного интерфейса позволяет конвертировать
 * строковый аргумент, отправленный игроком в нужный аргумент.
 */
public interface ArgumentParser<T> {

    /**
     * Конвертация строки в аргумент.
     *
     * @param value Строка, которую конвертируем в аргумент.
     * @return Аргумент, который получился после конвертации.
     */
    Argument<T> parseArgument(String value);

    class StringArgumentParser implements ArgumentParser<String> {
        @Override
        public Argument<String> parseArgument(String value) {
            if(value == null || value.isEmpty()) {
                return new Argument<>(FAIL, null);
            }

            return new Argument<>(SUCCESS, value);
        }
    }

    class IntegerArgumentParser implements ArgumentParser<Integer> {
        @Override
        public Argument<Integer> parseArgument(String value) {
            try {
                return new Argument<>(SUCCESS, Integer.parseInt(value));
            } catch(NumberFormatException e) {
                return new Argument<>(FAIL, 0);
            }
        }
    }

    class BooleanArgumentParser implements ArgumentParser<Boolean> {
        @Override
        public Argument<Boolean> parseArgument(String value) {
            if(value == null || value.isEmpty()) {
                return new Argument<>(FAIL, false);
            }

            return new Argument<>(SUCCESS, Boolean.parseBoolean(value));
        }
    }

    class FloatArgumentParser implements ArgumentParser<Float> {
        @Override
        public Argument<Float> parseArgument(String value) {
            try {
                return new Argument<>(SUCCESS, Float.parseFloat(value));
            } catch(NumberFormatException e) {
                return new Argument<>(FAIL, 0F);
            }
        }
    }

    class DoubleArgumentParser implements ArgumentParser<Double> {
        @Override
        public Argument<Double> parseArgument(String value) {
            try {
                return new Argument<>(SUCCESS, Double.parseDouble(value));
            } catch (NumberFormatException e) {
                return new Argument<>(FAIL, 0D);
            }
        }
    }

}
