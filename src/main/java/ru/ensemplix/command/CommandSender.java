package ru.ensemplix.command;

/**
 * Пользователь который отправил команду.
 */
public interface CommandSender {

    /**
     * Отправить сообщение пользователю.
     *
     * @param message Сообщение, которое отправляем пользователю.
     */
    void sendMessage(String message);

    /**
     * Отправить сообщение пользователю с форматированием.
     *
     * @param message Сообщение, которое отправляем пользователю.
     * @param args Аргументы, которые будут подставлены в строку.
     */
    default void sendMessage(String message, Object... args) {
        sendMessage(String.format(message, args));
    }

    /**
     * Может ли пользователь выполнять указанную команду.
     *
     * @param command Команду, которую пытается выполнить пользователь.
     * @param action Действие, которе пытается выполнить пользователь.
     * @return {@code true}, если может выполнять команду.
     */
    default boolean canUseCommand(String command, String action) {
        return true;
    }

}
