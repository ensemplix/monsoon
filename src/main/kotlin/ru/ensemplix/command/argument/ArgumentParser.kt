package ru.ensemplix.command.argument

import ru.ensemplix.command.CommandContext

/**
 * Реализация данного интерфейса позволяет конвертировать
 * строковый аргумент, отправленный игроком в нужный аргумент.
 */
interface ArgumentParser<out T> {

    /**
     * Конвертация строки в аргумент.
     *
     * @param value Строка, которую конвертируем в аргумент.
     *
     * @return Аргумент, который получился после конвертации.
     */
    fun parseArgument(context: CommandContext, index: Int, value: String?): Argument<T>?

}
