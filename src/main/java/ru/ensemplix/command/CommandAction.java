package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Представляет действие (подпункт) команды.
 */
@AllArgsConstructor
class CommandAction {

    /**
     * Метод, к которому относится действие.
     */
    @Getter
    private final Method method;

    /**
     * Аннотация, содержащая информацию о действии.
     */
    @Getter
    private final Command annotation;

}
