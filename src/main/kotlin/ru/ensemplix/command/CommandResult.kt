package ru.ensemplix.command

import ru.ensemplix.command.argument.Argument

/**
 * Результат выполнения команды.
 */
class CommandResult(context: CommandContext, arguments: List<Argument<*>>, success: Boolean)