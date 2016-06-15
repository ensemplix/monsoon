package ru.ensemplix.command;

import com.google.common.base.CharMatcher;
import ru.ensemplix.command.argument.Argument;
import ru.ensemplix.command.argument.ArgumentParser;
import ru.ensemplix.command.exception.CommandAccessException;
import ru.ensemplix.command.exception.CommandException;
import ru.ensemplix.command.exception.CommandNotFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.ensemplix.command.argument.ArgumentParser.*;

/**
 * Основной класс для работы с командами.
 */
public class CommandDispatcher {

    /**
     * Список автоматических дополнений команды.
     */
    protected static final Map<Class, CommandCompleter> completers = new HashMap<>();

    /**
     * Список команд.
     */
    protected static final Map<String, CommandHandler> commands = new HashMap<>();

    /**
     * Список парсеров в объекты.
     */
    protected static final Map<Class, ArgumentParser> parsers = new HashMap<>();

    /**
     * Нужно ли убирать первый символ("/", "!", "@") при выполнении команды.
     * По умолчанию символ убирается.
     */
    private final boolean removeFirstChar;

    public CommandDispatcher() {
        this(true);
    }

    public CommandDispatcher(boolean removeFirstChar) {
        this.removeFirstChar = removeFirstChar;

        // Примитивные парсеры.
        bind(String.class, new StringArgumentParser());
        bind(Integer.class, new IntegerArgumentParser());
        bind(int.class, new IntegerArgumentParser());
        bind(Boolean.class, new BooleanArgumentParser());
        bind(boolean.class, new BooleanArgumentParser());
        bind(Float.class, new FloatArgumentParser());
        bind(float.class, new FloatArgumentParser());
        bind(Double.class, new StringArgumentParser());
        bind(double.class, new StringArgumentParser());
    }

    /**
     * Выполнение команды, отправленной пользователем, на основе отправленного текста.
     *
     * Если команда не существует или нет такого действия, то будет выброшено исключение.
     * {@link CommandNotFoundException} CommandNotFoundException.
     *
     * Если пользователю нельзя выполнять указанную команду, то будет выброшено
     * исключение {@link CommandAccessException} CommandAccessException.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return {@code true}, если команда была выполнена без ошибок.
     * @throws CommandException Выбрасывает исключение, если команды не
     * существует или нет разрешения на ее выполнение.
     */
    public CommandResult call(CommandSender sender, String cmd) throws CommandException {
        CommandContext context = validate(sender, cmd);
        CommandAction action = context.getAction();

        if(action == null) {
            throw new CommandNotFoundException();
        }

        Method method = action.getMethod();
        String[] args = context.getArgs();

        Parameter[] parameters = method.getParameters();
        int length = parameters.length;

        List<Argument<?>> arguments = new ArrayList<>();
        Object[] parsed = new Object[length];
        parsed[0] = sender;

        for(int i = 1; i < length; i++) {
            Class<?> parameterType = parameters[i].getType();
            ArgumentParser parser;

            if(Iterable.class.isAssignableFrom(parameterType) || Argument.class.isAssignableFrom(parameterType)) {
                ParameterizedType type = (ParameterizedType) parameters[i].getParameterizedType();
                parser = parsers.get(type.getActualTypeArguments()[0]);
            } else {
                parser = parsers.get(parameterType);
            }

            if(Iterable.class.isAssignableFrom(parameterType)) {
                // Подготоваливаем коллекцию.
                Collection<Object> collection = new ArrayList<>();

                for(int y = i - 1; y < args.length; y++) {
                    Argument argument = parser.parseArgument(args[y]);

                    if(Argument.class.isAssignableFrom(parameterType)) {
                        collection.add(argument);
                    } else {
                        collection.add(argument.getValue());
                    }

                    if(argument.getText() == null) {
                        argument.setText(args[y]);
                    }

                    arguments.add(argument);
                }

                parsed[i] = collection;
            } else {
                // Подготавливаем аргументы команды.
                Argument argument;

                if (args.length + 1 > i) {
                    argument = parser.parseArgument(args[i - 1]);

                    if(argument.getText() == null) {
                       argument.setText(args[i - 1]);
                    }
                } else {
                    argument = parser.parseArgument(null);
                }

                if(Argument.class.isAssignableFrom(parameterType)) {
                    parsed[i] = argument;
                } else {
                    parsed[i] = argument.getValue();
                }

                arguments.add(argument);
            }
        }

        // Выполняем команду.
        try {
            // Если возвращает void, то считаем что результат выполнения команды всегда положительный.
            Object result = method.invoke(context.getHandler().getObject(), parsed);
            boolean success = result == null || (boolean) result;
            return new CommandResult(context, arguments, success);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Автоматическое дополнение команды на основе ввода пользователя.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return Возвращает список возможных вариантов автодополнения.
     */
    public Collection<String> complete(CommandSender sender, String cmd) {
        CommandContext context;

        try {
            context = validate(sender, cmd);
        } catch(CommandException e) {
            Collection<String> names = commands.keySet();

            if(removeFirstChar) {
                cmd = cmd.substring(1);
            }

            final String cmdFinal = cmd;

            if(cmd.length() > 0) {
                return names.stream().filter(name -> name.startsWith(cmdFinal)).collect(Collectors.toList());
            }

            return names;
        }

        String action = context.getActionName();
        String[] args = context.getArgs();

        if(action == null && context.getHandler().getMain() == null) {
            Collection<String> actions = context.getHandler().getActions().keySet();

            if(args.length == 1) {
                return actions.stream().filter(name -> name.startsWith(args[0])).collect(Collectors.toList());
            } else {
                return actions;
            }
        }

        String arg = "";
        int i = 1;

        if(args.length > 0) {
            i = args.length;
            arg = args[i - 1];
        }

        Parameter[] parameters = context.getAction().getMethod().getParameters();
        CommandCompleter completer;

        if(Iterable.class.isAssignableFrom(parameters[i].getType())) {
            ParameterizedType type = (ParameterizedType) parameters[i].getParameterizedType();
            completer = completers.get(type.getActualTypeArguments()[0]);
        } else {
            completer = completers.get(parameters[i].getType());
        }

        if (completer != null) {
            return completer.complete(context, arg);
        }

        return Collections.emptyList();
    }

    /**
     * Проверяет строку и конвертирует результат проверки в объект
     * {@link CommandContext} CommandContext.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return Возвращает результат проверки.
     * @throws CommandException Выбрасывает исключение, если команды не
     * существует или нет разрещения на ее выполнение.
     */
    private CommandContext validate(CommandSender sender, String cmd) throws CommandException {
        checkNotNull(sender, "Please provide command sender");

        if(cmd == null || cmd.length() <= 1) {
            throw new CommandNotFoundException();
        }

        if(removeFirstChar) {
            cmd = cmd.substring(1);
        }

        String[] args = cmd.split(" ");
        CommandHandler handler = commands.get(args[0]);

        if(handler == null) {
            throw new CommandNotFoundException();
        }

        Map<String, CommandAction> actions = handler.getActions();
        CommandAction action = null;

        if(args.length > 1 && actions.containsKey(args[1])) {
            action = actions.get(args[1]);
            args = Arrays.copyOfRange(args, 2, args.length);
        } else {
            args = Arrays.copyOfRange(args, 1, args.length);

            if(handler.getMain() != null) {
                action = handler.getMain();
            }
        }

        String actionName = null;

        if(action != null) {
            Method main = handler.getMain().getMethod();
            Method method = action.getMethod();

            if(main != null) {
                actionName = main.equals(method) ? null : method.getName();
            }

            if(action.getAnnotation().permission()) {
                if (!sender.canUseCommand(handler.getName(), actionName)) {
                    throw new CommandAccessException();
                }
            }
        }

        return new CommandContext(handler.getName(), actionName, action, args, handler);
    }

    /**
     * Регистрация команды происходит по методам, которые содержат аннотацию
     * {@link Command} @Command. Количество имен для команды неограничено.
     * Обязательно должна быть хотя бы одна команда.
     *
     * @param object Объект, в котором мы ищем команды.
     * @param names Названия команд.
     */
    public void register(Object object, String... names) {
        checkNotNull(object, "Please provide command object");

        // Проверяем, что команды с таким именем еще нет.
        for(String name : names) {
            if(name == null || name.length() <= 0) {
                throw new IllegalArgumentException("Please provide valid command name");
            }

            if(CharMatcher.WHITESPACE.matchesAnyOf(name)) {
                throw new IllegalArgumentException("Please provide command name with no whitespace");
            }

            if(commands.containsKey(name)) {
                throw new IllegalArgumentException("Command with name " + name + " already exists");
            }
        }

        Map<String, CommandAction> actions = new HashMap<>();
        CommandAction main = null;

        for (Method method : object.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            // Команда обязательно должна быть помечена аннотацией @Command.
            if(annotation == null) {
                continue;
            }

            // Команда должна обязательно возвращать void или boolean.
            if(method.getReturnType() != void.class && method.getReturnType() != boolean.class) {
                throw new IllegalArgumentException(method.getName() + " must return void or boolean");
            }

            Parameter[] parameters = method.getParameters();
            int length = parameters.length;

            // Первым параметром команды обязательно должен быть ее отправитель.
            if(length == 0 || !CommandSender.class.isAssignableFrom(parameters[0].getType())) {
                throw new IllegalArgumentException("Please provide command sender for " + method.getName());
            }

            // Проверяем, что все параметры команды будут отработаны корректно.
            for(int i = 1; i < length; i++) {
                Class<?> parameterType = parameters[i].getType();

                if(Iterable.class.isAssignableFrom(parameterType)) {
                    if (i + 1 != length) {
                        throw new IllegalArgumentException("Iterable must be last parameter in " + method.getName());
                    }

                } else {
                    if(Argument.class.isAssignableFrom(parameterType)) {
                        ParameterizedType type = (ParameterizedType) parameters[i].getParameterizedType();
                        parameterType = (Class<?>) type.getActualTypeArguments()[0];
                    }

                    if(!parsers.containsKey(parameterType)) {
                        throw new IllegalArgumentException("Please provide type parser for " + parameters[i].getType());
                    }
                }
            }

            CommandAction action = new CommandAction(method, annotation);

            if(annotation.main()) {
                main = action;
            }

            actions.put(method.getName(), action);
        }

        if(actions.isEmpty()) {
            throw new IllegalStateException("Not found any method marked with @Command");
        }

        for(String name : names) {
            commands.put(name, new CommandHandler(names[0], object, main, actions));
        }
    }

    /**
     * Удаляет все команды, связанные с выбранным классом.
     *
     * @param cls Класс, который мы удаляем из команд.
     */
    public void unregister(Class cls) {
        checkNotNull(cls, "Please provide command class");

        Iterator<CommandHandler> iterator = commands.values().iterator();

        while(iterator.hasNext()) {
            if(iterator.next().getObject().getClass().equals(cls)) {
                iterator.remove();
            }
        }
    }

    /**
     * Регистрация парсера для конвертации строки в объект.
     *
     * @param clz Класс, который мы будем конвертировать в объект.
     * @param parser Парсер, который знает как парсить класс.
     */
    public void bind(Class<?> clz, ArgumentParser parser) {
        parsers.put(clz, parser);
    }

    /**
     * Регистрация дополнителя для автодополнения команды.
     *
     * @param clz Класс, который мы будем автодополнять.
     * @param completer Дополнитель, который знает как дополнять класс.
     */
    public void bind(Class<?> clz, CommandCompleter completer) {
        completers.put(clz, completer);
    }

}
