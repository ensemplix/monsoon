package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
public class CommandHandler {

    @Getter
    private String name;

    @Getter
    private Method method;

    @Getter
    private Object object;

}
