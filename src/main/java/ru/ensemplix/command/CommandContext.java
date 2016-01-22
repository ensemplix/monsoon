package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Результат проверки строки на наличие команды.
 */
@AllArgsConstructor
public class CommandContext {

    /**
     * Команда, которую пользователь хочет выполнить.
     */
    @Getter
    private Method method;

    /**
     * Действие команды, которое хочет выполнить пользователь.
     */
    @Getter
    private String action;

    /**
     * Аргументы для команды, которые передал пользователь в строке.
     */
    @Getter
    private String[] args;

    /**
     * Обработчик, который содержит информацию о вызываемой команде.
     */
    @Getter
    private CommandHandler handler;

}
