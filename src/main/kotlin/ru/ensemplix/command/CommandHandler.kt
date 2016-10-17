package ru.ensemplix.command

/**
 * Содержит в себе результат парсинга объекта на команды.
 */
class CommandHandler(val name: String, val obj: Any, val main: CommandAction, val actions: Map<String, CommandAction>)