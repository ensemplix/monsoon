package ru.ensemplix.command

/**
 * Пользователь который отправил команду.
 */
interface CommandSender {

    /**
     * Отправить сообщение пользователю.
     *
     * @param message Сообщение, которое отправляем пользователю.
     * @param args Аргументы, которые будут подставлены в строку.
     */
    fun sendMessage(message: String, vararg args: Any)

}
