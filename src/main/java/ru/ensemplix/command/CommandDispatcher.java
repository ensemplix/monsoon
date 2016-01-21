package ru.ensemplix.command;

import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.ensemplix.command.TypeParser.*;

/**
 * Основной класс для работы с командами. Здесь регистрируются команды,
 * парсеры и выполняются команды.
 */
public class CommandDispatcher {

    /**
     * Список всех команд.
     */
    protected static final Map<String, CommandHandler> commands = new HashMap<>();

    /**
     * Список всех парсеров.
     */
    protected static final Map<Class, TypeParser> parsers = new HashMap<>();

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

        // Стандартные парсеры библиотеки.
        bind(String.class, new StringParser());
        bind(Integer.class, new IntegerParser());
        bind(int.class, new IntegerParser());
        bind(Boolean.class, new BooleanParser());
        bind(boolean.class, new BooleanParser());
        bind(Float.class, new FloatParser());
        bind(float.class, new FloatParser());
        bind(Double.class, new DoubleParser());
        bind(double.class, new DoubleParser());
    }

    /**
     * Выполнении команды, отправленной пользователем. Если команда не будет найдена, то
     * будет брошено исключение CommandNotFoundException. Возвращаемый результат зависит
     * от результата выполнения команды и может использоваться для логирования.
     */
    public boolean call(CommandSender sender, String cmd) throws CommandNotFoundException, CommandAccessException {
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

        Map<String, Method> methods = handler.getMethods();
        Method method = null;
        int start = 0;

        if(args.length > 1 && methods.containsKey(args[1])) {
            method = methods.get(args[1]);
            start = 1;
        } else if(handler.getMain() != null) {
            method = handler.getMain();
        }

        // Если команда не найдена, то выбрасываем исключение.
        if(method == null) {
            throw new CommandNotFoundException();
        }

        String action = handler.getMain().equals(method) ? null : method.getName();

        if(!sender.canUseCommand(handler.getName(), action)) {
            throw new CommandAccessException();
        }

        Parameter[] parameters = method.getParameters();
        int length = parameters.length;

        Object[] parsed = new Object[length];
        parsed[0] = sender;

        for (int i = 1; i < length; i++) {
            int current = start + i;

            // Подготоваливаем коллекцию.
            if(Iterable.class.isAssignableFrom(parameters[i].getType())) {
                ParameterizedType type = (ParameterizedType) parameters[i].getParameterizedType();
                TypeParser parser = parsers.get(type.getActualTypeArguments()[0]);
                Collection<Object> collection = new LinkedList<>();

                for(int y = current; y < args.length; y++) {
                    collection.add(parser.parse(args[y]));
                }

                parsed[i] = collection;
            } else {
                // Подготавливаем аргументы команды.
                TypeParser parser = parsers.get(parameters[i].getType());

                if (args.length > current) {
                    parsed[i] = parser.parse(args[current]);
                } else {
                    parsed[i] = parser.parse(null);
                }
            }
        }

        // Выполняем команду.
        try {
            Object result = method.invoke(handler.getObject(), parsed);
            return result == null || (boolean) result;
        } catch (Exception e) {
            Throwables.propagate(e);
        }

        return false;
    }

    /**
     * Регистрация команды происходит по любому объекту с помощью аннотации @Command.
     * Количество имен команды на ограничено.
     */
    public void register(Object object, String... names) {
        checkNotNull(object, "Please provide command object");
        checkArgument(names.length > 0, "Please provide command name");

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

        Map<String, Method> actions = new HashMap<>();
        Method main = null;

        for (Method method : object.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);

            // Команда должна обязательно быть помечена аннотацией @Command.
            if(annotation == null) {
                continue;
            }

            // Команда должна обязательно возвращать void или boolean.
            if(method.getReturnType() != void.class && method.getReturnType() != boolean.class) {
                throw new IllegalArgumentException(method.getName() + " must return void or boolean");
            }

            Parameter[] parameters = method.getParameters();
            int length = parameters.length;

            // Обязательно первым параметром команды должен быть ее отправитель.
            if(length == 0 || !CommandSender.class.isAssignableFrom(parameters[0].getType())) {
                throw new IllegalArgumentException("Please provide command sender for " + method.getName());
            }

            // Проверяем что все параметры команды будут отработаны корректно.
            for(int i = 1; i < length; i++) {
                if(Iterable.class.isAssignableFrom(parameters[i].getType())) {
                    if(i + 1 != length) {
                        throw new IllegalArgumentException("Iterable must be last parameter in " + method.getName());
                    }
                } else if(!parsers.containsKey(parameters[i].getType())) {
                    throw new IllegalArgumentException("Please provide type parser for " + parameters[i].getType());
                }
            }

            if(annotation.main()) {
                main = method;
            }

            actions.put(method.getName(), method);
        }

        if(actions.isEmpty()) {
            throw new IllegalStateException("Not found any method marked with @Command");
        }

        for(String name : names) {
            commands.put(name, new CommandHandler(names[0], object, main, actions));
        }
    }

    /**
     * Регистрация парсера для конвертации строки в объект.
     */
    public void bind(Class<?> clz, TypeParser parser) {
        parsers.put(clz, parser);
    }
    
}
