package ru.ensemplix.command.simple;

import ru.ensemplix.command.CommandSender;

public class SimpleSender implements CommandSender {

    @Override
    public void sendMessage(String message, Object... args) {
        System.out.printf(message, args);
    }

}
