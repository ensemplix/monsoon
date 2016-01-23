package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Представляет действие (подпункт) команды.
 */
@AllArgsConstructor
public class CommandAction {

    /**
     * Метод, к которому относится действие.
     */
    @Getter
    private Method method;

    /**
     * Аннотация, содержащая информацию о действии.
     */
    @Getter
    private Command annotation;

}
