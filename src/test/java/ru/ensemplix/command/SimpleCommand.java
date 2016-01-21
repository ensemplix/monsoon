package ru.ensemplix.command;

import java.util.Collection;

public class SimpleCommand {

    public boolean hello = false;
    public boolean test = false;
    public int integer;
    public String string;
    public Collection<String> strings;

    @Command
    public boolean hello(SimpleSender sender) {
        hello = true;
        return false;
    }

    @Command(main = true)
    public void test(CommandSender sender) {
        test = true;
    }

    @Command
    public boolean integer(CommandSender sender, int value) {
        if(value != 0) {
            integer = value;
            return true;
        }

        return false;
    }

    @Command
    public void string(CommandSender sender, String value) {
        string = value;
    }

    @Command
    public void collection(CommandSender sender, Collection<String> values) {
        strings = values;
    }

}
