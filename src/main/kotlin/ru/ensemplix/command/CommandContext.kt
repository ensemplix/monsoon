package ru.ensemplix.command

/**
 * Результат проверки строки на наличие команды.
 */
class CommandContext(val commandName: String,
                     val actionName: String?,
                     val action: CommandAction?,
                     val args: Array<String>,
                     val handler: CommandHandler,
                     val permission: String?)
