package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Map;

@AllArgsConstructor
public class CommandHandler {

    /**
     * Основное название команды.
     */
    @Getter
    private String name;

    /**
     * Объект, который вызываем при выполнение команды.
     */
    @Getter
    private Object object;

    @Getter
    private Method main;

    @Getter
    private Map<String, Method> methods;

}
