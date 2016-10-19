package ru.ensemplix.command.simple;

import ru.ensemplix.command.CommandSender;

public class SimpleSender implements CommandSender {

    @Override
    public void sendMessage(String message, Object... args) {
        System.out.printf(message, args);
    }

    @Override
    public boolean canUseCommand(String command, String action) {
        if((command.equals("access") || command.equals("access2")) && action == null) {
            return false;
        }

        if(command.equals("access3") && action.equals("test")) {
            return false;
        }

        return true;
    }

}
