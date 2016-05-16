package ru.ensemplix.command;

/**
 * Реализация данного интерфейса позволяет конвертировать
 * строковый аргумент, отправленный игроком в нужный объект.
 */
public interface CommandArgumentParser<T> {

    /**
     * Конвертация строки в объект.
     *
     * @param value Строку, которую конвертируем в объект.
     * @return Объект, который получился после конвертации.
     */
    T parseArgument(String value);

    class StringArgumentParser implements CommandArgumentParser<String> {
        @Override
        public String parseArgument(String value) {
            return value;
        }
    }

    class IntegerArgumentParser implements CommandArgumentParser<Integer> {
        @Override
        public Integer parseArgument(String value) {
            try {
                return Integer.parseInt(value);
            } catch(NumberFormatException e) {
                return 0;
            }
        }
    }

    class BooleanArgumentParser implements CommandArgumentParser<Boolean> {
        @Override
        public Boolean parseArgument(String value) {
            return Boolean.parseBoolean(value);
        }
    }

    class FloatArgumentParser implements CommandArgumentParser<Float> {
        @Override
        public Float parseArgument(String value) {
            try {
                return Float.parseFloat(value);
            } catch(NumberFormatException e) {
                return 0F;
            }
        }
    }

    class DoubleArgumentParser implements CommandArgumentParser<Double> {
        @Override
        public Double parseArgument(String value) {
            try {
                return Double.parseDouble(value);
            } catch(NumberFormatException e) {
                return 0D;
            }
        }
    }

}
