package ru.ensemplix.command.argument

import ru.ensemplix.command.argument.Argument.Result.FAIL
import ru.ensemplix.command.argument.Argument.Result.SUCCESS

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
    fun parseArgument(value: String?): Argument<T>

}
