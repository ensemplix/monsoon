package ru.ensemplix.command

/**
 * Реализация данного интерфейса позволяет создавать автоматическое
 * дополнение команды.
 */
interface CommandCompleter {

    /**
     * Автодополнение на основе переданной строки.
     *
     * @param context Результат проверки строки на наличие команды.
     * @param arg Часть строки, по которой мы будем искать.
     * @return Возвращает список возможных вариантов дополнения.
     */
    fun complete(context: CommandContext, arg: String): Collection<String>

}
