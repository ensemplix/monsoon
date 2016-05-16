package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Содержит в себе результат парсинга объекта на команды.
 */
@AllArgsConstructor
public class CommandHandler {

    /**
     * Основное название команды.
     */
    @Getter
    private final String name;

    /**
     * Объект, который вызываем при выполнении команды.
     */
    @Getter
    private final Object object;

    /**
     * Обозначает, что выбранный метод команды является главным.
     *
     * В случае, если не будет найдено альтернатив, будет
     * использоваться данный метод.
     */
    @Getter
    private final CommandAction main;

    /**
     * Список всех действий команды внутри объекта.
     */
    @Getter
    private final Map<String, CommandAction> actions;

}
