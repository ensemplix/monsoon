package ru.ensemplix.command

import java.lang.reflect.Method

/**
 * Представляет действие (подпункт) команды.
 */
class CommandAction(val method: Method, val annotation: Command)
