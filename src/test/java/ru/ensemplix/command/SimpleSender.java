package ru.ensemplix.command;

public class SimpleSender implements CommandSender {

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }

}
