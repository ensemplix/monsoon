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
     * Может ли пользователь выполнять указанную команду.
     */

    /**
     * Может ли пользователь выполнять указанную команду.
     *
     * @param command Команду, которую пытается выполнить пользователь.
     * @param action Действие, которе пытается выполнить пользователь.
     * @return {@code true}, если может выполнять команду.
     */
    boolean canUseCommand(String command, String action);

}
