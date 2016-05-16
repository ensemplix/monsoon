package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Результат проверки строки на наличие команды.
 */
@AllArgsConstructor
public class CommandContext {

    /**
     * Название команды, которое хочет выполнить пользователь.
     */
    @Getter
    private final String name;

    /**
     * Команда, которую пользователь хочет выполнить.
     */
    @Getter
    private final CommandAction action;

    /**
     * Аргументы для команды, которые передал пользователь в строке.
     */
    @Getter
    private final String[] args;

    /**
     * Обработчик, который содержит информацию о вызываемой команде.
     */
    @Getter
    private final CommandHandler handler;

}
