package ru.ensemplix.command.argument

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
class Argument<out T>(val result: Argument.Result, val value: T?) {
    var text: String? = null

    /**
     * Типы результата конвертации аргумента.
     */
    enum class Result {
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
