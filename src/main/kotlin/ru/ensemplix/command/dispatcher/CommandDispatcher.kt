package ru.ensemplix.command.dispatcher

import ru.ensemplix.command.CommandCompleter
import ru.ensemplix.command.CommandContext
import ru.ensemplix.command.CommandResult
import ru.ensemplix.command.CommandSender
import ru.ensemplix.command.argument.ArgumentParser

interface CommandDispatcher {

    /**
     * Проверяет строку и конвертирует результат проверки в объект
     * {@link CommandContext} CommandContext.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return Возвращает результат проверки.
     */
    fun validate(sender: CommandSender, cmd: String?): CommandContext?

    /**
     * Выполнение команды, отправленной пользователем, на основе отправленного текста.
     *
     * Если команда не существует или нет такого действия, то будет выброшено исключение.
     * {@link CommandNotFoundException} CommandNotFoundException.
     *
     * Если пользователю нельзя выполнять указанную команду, то будет выброшено
     * исключение {@link CommandAccessException} CommandAccessException.
     *
     * @param sender Отправитель команды.
     * @param context Результат проверки переданной строки.
     * @return {@code true}, если команда была выполнена без ошибок.
     */
    fun call(sender: CommandSender, context: CommandContext): CommandResult

    /**
     * Автоматическое дополнение команды на основе ввода пользователя.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return Возвращает список возможных вариантов автодополнения.
     */
    fun complete(sender: CommandSender, cmd: String): Collection<String>

    /**
     * Регистрация команды происходит по методам, которые содержат аннотацию
     * {@link Command} @Command. Количество имен для команды неограничено.
     * Обязательно должна быть хотя бы одна команда.
     *
     * @param obj Объект, в котором мы ищем команды.
     * @param names Названия команд.
     */
    fun register(obj: Any, vararg names: String?)

    /**
     * Удаляет все команды, связанные с выбранным классом.
     *
     * @param cls Класс, который мы удаляем из команд.
     */
    fun unregister(cls: Class<*>)

    /**
     * Регистрация парсера для конвертации строки в объект.
     *
     * @param clz Класс, который мы будем конвертировать в объект.
     * @param parser Парсер, который знает как парсить класс.
     */
    fun bind(clz: Class<*>, parser: ArgumentParser<*>)

    /**
     * Регистрация дополнителя для автодополнения команды.
     *
     * @param clz Класс, который мы будем автодополнять.
     * @param completer Дополнитель, который знает как дополнять класс.
     */
    fun bind(clz: Class<*>, completer: CommandCompleter)

}
