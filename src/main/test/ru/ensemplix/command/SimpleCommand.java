package ru.ensemplix.command;

public class SimpleCommand {

    public boolean hello = false;
    public boolean test = false;

    @Command
    public boolean hello(SimpleSender sender) {
        hello = true;
        return true;
    }

    @Command
    public void test(CommandSender sender) {
        test = true;
    }

}
