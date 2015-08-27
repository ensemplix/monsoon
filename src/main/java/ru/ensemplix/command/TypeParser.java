package ru.ensemplix.command;

/**
 * Реализация данного интерфейса позволяет конвертировать
 * строковый аргумент, отправленный игроком в нужный объект.
 */
public interface TypeParser<T> {

    /**
     * Конвертация строки в объект.
     */
    T parse(String value);

}
