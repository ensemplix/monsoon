package ru.ensemplix.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.ensemplix.command.argument.Argument;

import java.util.List;

/**
 * Результат выполнения команды.
 */
@AllArgsConstructor
public class CommandResult {

    /**
     * Результат проверки строки на наличие команды.
     */
    @Getter
    private final CommandContext context;

    /**
     * Аргументы, которые были получены в ходе конвертации строки в объекты.
     */
    @Getter
    private final List<Argument<?>> arguments;

    /**
     * Удалось ли выполнить команду без каких-либо проблем.
     *
     * В качестве проблемы могут выступать некорректно переданные параметры.
     */
    @Getter
    private final boolean success;

}
