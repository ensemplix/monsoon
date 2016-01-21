package ru.ensemplix.command.simple;

import ru.ensemplix.command.CommandSender;

public class SimpleSender implements CommandSender {

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

    @Override
    public boolean canUseCommand(String command, String action) {
        return !command.equals("access");
    }

}
