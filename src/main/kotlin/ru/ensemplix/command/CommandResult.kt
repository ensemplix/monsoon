package ru.ensemplix.command

import ru.ensemplix.command.argument.Argument

/**
 * Результат выполнения команды.
 */
class CommandResult(val context: CommandContext, val arguments: List<Argument<*>?>, val isSuccess: Boolean)
