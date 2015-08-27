package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
public class CommandHandler {

    /**
     * Основное название команды.
     */
    @Getter
    private String origin;

    /**
     * Подтип команды. Например при выполнение команды
     * "/region create" подтипом будет create.
     */
    @Getter
    private String name;

    /**
     * Метод, который вызываем при выполнение команды.
     */
    @Getter
    private Method method;

    /**
     * Объект, который вызываем при выполнение команды.
     */
    @Getter
    private Object object;

}
