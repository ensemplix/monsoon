package ru.ensemplix.command.simple;

import ru.ensemplix.command.Command;
import ru.ensemplix.command.CommandSender;
import ru.ensemplix.command.argument.Argument;

import java.util.Collection;

public class SimpleCommand {

    public boolean hello = false;
    public boolean test = false;
    public int integer;
    public String string;
    public Collection<String> strings;
    public Argument argument;
    public Argument argument2;

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

    @Command
    public void argument(CommandSender sender, Argument<String> value) {
        argument = value;
    }

    @Command
    public void argument2(CommandSender sender, Argument<String> value) {
        argument2 = value;
    }

}
