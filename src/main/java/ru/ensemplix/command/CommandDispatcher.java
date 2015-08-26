package ru.ensemplix.command;

import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class CommandDispatcher {

    private final Multimap<String, CommandHandler> commands = ArrayListMultimap.create();

    public boolean call(CommandSender sender, String cmd) throws CommandNotFoundException {
        checkNotNull(sender, "Please provide command sender");
        checkNotNull(cmd, "Please provide command line");
        checkArgument(cmd.length() > 1, "Please provide valid command line");

        String[] args = cmd.substring(1).split(" ");
        CommandHandler command = getCommand(args[0], args[0]);

        if(args.length > 1) {
            CommandHandler subcommand = getCommand(args[0], args[1]);

            if(subcommand != null) {
                command = subcommand;
            }
        }

        if(command == null) {
            throw new CommandNotFoundException();
        }

        try {
            Object result = command.getMethod().invoke(command.getObject(), sender);
            return result == null || (boolean) result;
        } catch (Exception e) {
            Throwables.propagate(e);
        }

        return false;
    }

    public void register(Object obj, String... names) {
        checkNotNull(obj, "Please provide valid command");
        checkNotNull(names, "Please provide valid command name");

        for (Method method : obj.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            if(annotation == null) {
                continue;
            }

            Parameter[] parameters = method.getParameters();

            if(parameters.length == 0 || !CommandSender.class.isAssignableFrom(parameters[0].getType())) {
                throw new IllegalArgumentException("Please provide command sender for " + method);
            }

            CommandHandler handler = new CommandHandler(method.getName(), method, obj);

            for(String name : names) {
                commands.put(name, handler);
            }
        }
    }

    private CommandHandler getCommand(String command, String subCommand) {
        for (CommandHandler handler : commands.get(command)) {
             if(handler.getName().equals(subCommand)) {
                 return handler;
             }
        }

        return null;
    }

}
