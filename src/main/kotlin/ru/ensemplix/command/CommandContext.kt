package ru.ensemplix.command
import lombok.AllArgsConstructor
import lombok.Getter

/**
 * Результат проверки строки на наличие команды.
 */
class CommandContext(val commandName: String,
                     val actionName: String,
                     val action: CommandAction?,
                     val args: Array<String>,
                     val handler: CommandHandler)