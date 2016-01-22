package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
public class CommandContext {

    @Getter
    private String name;

    @Getter
    private String action;

    @Getter
    private String[] args;

    @Getter
    private CommandHandler handler;

    @Getter
    private Method method;

}
