package ru.ensemplix.command.argument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Необязательная механика в аргументах команды, чтобы знать как прошла
 * конвертация в аргумент.
 *
 * Например, мы хотим создать команду, которая выдает конкретному игроку
 * предметы, а если не указан игрок, хотим чтобы предметы выдавались
 * тому, кто отправил команду.
 *
 * <pre><code>
 * public void give(CommandSender sender, Player target, Item item)
 * </code></pre>
 *
 * Если игрок с переданным ником не будет найден, то target равен {@code null}.
 * Мы не можем убедится, что имя игрока было передано.
 *
 * <pre><code>
 * public void give(CommandSender sender, Argument&lt;Player&gt; target, Item item)
 * </code></pre>
 *
 * Теперь, если не был передан аргумент с ником игрока, то аргумент будет равен {@code null}.
 * Если в ходе конвертации не нашелся игрок, то result будет {@code FAIL}.
 */
@AllArgsConstructor
public class Argument<T> {

    /**
     * Результат конвертации аргумента.
     */
    @Getter
    private final Result result;

    /**
     * Значение, которое получили в результате конвертации.
     */
    @Getter
    private final T value;

    /**
     * Аргумент, который использовался для конвертации.
     */
    @Setter @Getter
    private String text;

    public Argument(Result result, T value) {
        this.result = result;
        this.value = value;
    }

    /**
     * Типы результата конвертации аргумента.
     */
    public enum Result {

        /**
         * Аргумент успешно конвертирован.
         */
        SUCCESS,

        /**
         * Не удалось конвертировать аргумент.
         */
        FAIL

    }

}
