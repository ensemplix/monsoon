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

    /**
     * Может ли пользователь выполнять указанную команду.
     *
     * @param command Команду, которую пытается выполнить пользователь.
     * @param action Действие, которе пытается выполнить пользователь.
     * @return {@code true}, если может выполнять команду.
     */
    fun canUseCommand(command: String, action: String?): Boolean

}
