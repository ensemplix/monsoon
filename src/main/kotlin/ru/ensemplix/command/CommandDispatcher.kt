package ru.ensemplix.command
import ru.ensemplix.command.argument.Argument
import ru.ensemplix.command.argument.ArgumentParser
import ru.ensemplix.command.argument.ArgumentParser.*
import ru.ensemplix.command.exception.CommandAccessException
import ru.ensemplix.command.exception.CommandException
import ru.ensemplix.command.exception.CommandNotFoundException
import java.lang.reflect.ParameterizedType
import java.util.*
import java.util.stream.Collectors

/**
 * Основной класс для работы с командами.
 */
class CommandDispatcher(val removeFirstChar: Boolean = true) {

    companion object {
        /**
         * Список команд.
         */
        @JvmField
        val commands = HashMap<String, CommandHandler>()

        /**
         * Список автомачических дополнений команды.
         */
        @JvmField
        val completers = HashMap<Class<*>, CommandCompleter>()

        /**
         * Список парсеров в объекты.
         */
        @JvmField
        val parsers = HashMap<Class<*>, ArgumentParser<*>>()
    }

    init {
        // Примитивные парсеры.
        bind(String::class.java, StringArgumentParser())
        bind(Int::class.java, IntegerArgumentParser())
        bind(Integer.TYPE, IntegerArgumentParser())
        bind(Boolean::class.java, BooleanArgumentParser())
        bind(java.lang.Boolean.TYPE, BooleanArgumentParser())
        bind(Float::class.java, FloatArgumentParser())
        bind(java.lang.Float.TYPE, FloatArgumentParser())
        bind(Double::class.java, StringArgumentParser())
        bind(java.lang.Double.TYPE, StringArgumentParser())
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
    @Throws(CommandException::class)
    fun call(sender: CommandSender, cmd: String): CommandResult {
        val context = validate(sender, cmd)
        val action = context.action

        if (action == null) {
            throw CommandNotFoundException()
        }

        val method = action.method
        val args = context.args
        val parameters = method.getParameters()
        val length = parameters.size
        val arguments = ArrayList<Argument<*>>()
        val parsed = arrayOfNulls<Any>(length)
        parsed[0] = sender

        for (i in 1..length - 1) {
            val parameterType = parameters[i].getType()
            val parser: ArgumentParser<*>?

            if(Iterable::class.java.isAssignableFrom(parameterType) || Argument::class.java.isAssignableFrom(parameterType)) {
                val type = parameters[i].getParameterizedType() as ParameterizedType
                parser = parsers.get(type.getActualTypeArguments()[0])
            } else {
                parser = parsers.get(parameterType)
            }

            if(Iterable::class.java.isAssignableFrom(parameterType)) {
                // Подготоваливаем коллекцию.
                val collection = ArrayList<Any?>()

                for (y in i - 1..args.size - 1) {
                    val argument = parser!!.parseArgument(args[y])

                    if (Argument::class.java.isAssignableFrom(parameterType)) {
                        collection.add(argument)
                    } else {
                        collection.add(argument.value)
                    }

                    if(argument.text == null) {
                        argument.text = args[y]
                    }

                    arguments.add(argument)
                }
                parsed[i] = collection
            } else {
                // Подготавливаем аргументы команды.
                val argument: Argument<*>
                if(args.size + 1 > i) {
                    argument = parser!!.parseArgument(args[i - 1])
                    if (argument.text == null) {
                        argument.text = args[i - 1]
                    }
                }
                else {
                    argument = parser!!.parseArgument(null)
                }

                if (Argument::class.java.isAssignableFrom(parameterType)) {
                    parsed[i] = argument
                } else {
                    parsed[i] = argument.value
                }

                arguments.add(argument)
            }
        }
        // Выполняем команду.
        try {
            // Если возвращает void, то считаем что результат выполнения команды всегда положительный.
            val result = method.invoke(context.handler.obj, parsed)
            val success = result == null || result as Boolean
            return CommandResult(context, arguments, success)
        } catch (e:Exception) {
            throw RuntimeException(e)
        }
    }
    /**
     * Автоматическое дополнение команды на основе ввода пользователя.
     *
     * @param sender Отправитель команды.
     * @param cmd Строка, которую отослал отправитель.
     * @return Возвращает список возможных вариантов автодополнения.
     */
    fun complete(sender: CommandSender, cmd: String): Collection<String> {
        val context: CommandContext

        try {
            context = validate(sender, cmd)
        } catch (e: CommandException) {
            val names = commands.keys

            if(cmd.length > 0) {
                //return names.stream().filter({ name-> name.startsWith(cmd) }).collect(Collectors.toList())
            }

            return names
        }

        val action = context.actionName
        val args = context.args
        if(action == null && context.handler.main == null) {
            val actions = context.handler.actions.keys
            if (args.size == 1) {
                //return actions.stream().filter({ name-> name.startsWith(args[0]) }).collect(Collectors.toList())
                return actions
            } else {
                return actions
            }
        }

        var arg = ""
        var i = 1

        if(args.size > 0) {
            i = args.size
            arg = args[i - 1]
        }

        val parameters = context.action!!.method.parameters
        val parameterType = parameters[i].getType()
        val completer: CommandCompleter?

        if(Iterable::class.java.isAssignableFrom(parameterType) || Argument::class.java.isAssignableFrom(parameterType)) {
            val type = parameters[i].getParameterizedType() as ParameterizedType
            completer = completers.get(type.getActualTypeArguments()[0])
        }
        else {
            completer = completers.get(parameterType)
        }

        if (completer != null) {
            return completer.complete(context, arg)
        }

        return emptyList<String>()
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
    @Throws(CommandException::class)
    private fun validate(sender: CommandSender, cmd: String?): CommandContext {
        if(cmd == null || cmd.length <= 1) {
            throw CommandNotFoundException()
        }

        var args = cmd.split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val handler = commands.get(args[0])

        if(handler == null) {
            throw CommandNotFoundException()
        }

        val actions = handler.actions
        var action: CommandAction? = null

        if(args.size > 1 && actions.containsKey(args[1])) {
            action = actions.get(args[1])
            args = Arrays.copyOfRange<String>(args, 2, args.size)
        } else {
            args = Arrays.copyOfRange<String>(args, 1, args.size)

            if(handler.main != null) {
                action = handler.main
            }
        }

        var actionName: String? = null

        if (action != null) {
            val main = handler.main!!.method
            val method = action.method

            if(main != null) {
                actionName = if (main == method) null else method.getName()
            }

            if(action.annotation.permission) {
                if(!sender.canUseCommand(handler.name, actionName!!)) {
                    throw CommandAccessException()
                }
            }
        }

        return CommandContext(handler.name, actionName!!, action, args, handler)
    }
    /**
     * Регистрация команды происходит по методам, которые содержат аннотацию
     * {@link Command} @Command. Количество имен для команды неограничено.
     * Обязательно должна быть хотя бы одна команда.
     *
     * @param obj Объект, в котором мы ищем команды.
     * @param names Названия команд.
     */
    fun register(obj: Any, vararg names: String?) {
        // Проверяем, что команды с таким именем еще нет.
        for(name in names) {
            if(name == null || name.length <= 0) {
                throw IllegalArgumentException("Please provide valid command name")
            }

            //if(CharMatcher.WHITESPACE.matchesAnyOf(name)) {
            //    throw IllegalArgumentException("Please provide command name with no whitespace")
            //}

            if(commands.containsKey(name)) {
                throw IllegalArgumentException("Command with name " + name + " already exists")
            }
        }

        val actions = HashMap<String, CommandAction>()
        var main: CommandAction? = null

        for(method in obj.javaClass.methods) {
            val annotation = method.getAnnotation(Command::class.java)

            // Команда обязательно должна быть помечена аннотацией @Command.
            if(annotation == null) {
                continue
            }

            // Команда должна обязательно возвращать void или boolean.
            if(method.getReturnType() != Void.TYPE && method.getReturnType() != java.lang.Boolean.TYPE) {
                throw IllegalArgumentException(method.getName() + " must return void or boolean")
            }

            val parameters = method.getParameters()
            val length = parameters.size

            // Первым параметром команды обязательно должен быть ее отправитель.
            if(length == 0 || !CommandSender::class.java.isAssignableFrom(parameters[0].getType())) {
                throw IllegalArgumentException("Please provide command sender for " + method.getName())
            }

            // Проверяем, что все параметры команды будут отработаны корректно.
            for(i in 1..length - 1)  {
                var parameterType = parameters[i].getType()

                if(Iterable::class.java.isAssignableFrom(parameterType)) {
                    if(i + 1 != length) {
                        throw IllegalArgumentException("Iterable must be last parameter in " + method.getName())
                    }
                } else {
                    if(Argument::class.java.isAssignableFrom(parameterType)) {
                        val type = parameters[i].getParameterizedType() as ParameterizedType
                        parameterType = type.getActualTypeArguments()[0] as Class<*>
                    }

                    if(!parsers.containsKey(parameterType)) {
                        throw IllegalArgumentException("Please provide type parser for " + parameters[i].getType())
                    }
                }
            }

            val action = CommandAction(method, annotation)
            if(annotation.main) {
                main = action
            }

            actions.put(method.getName(), action)
        }

        if(actions.isEmpty()) {
            throw IllegalStateException("Not found any method marked with @Command")
        }

        for(name in names) {
            commands.put(name!!, CommandHandler(names[0]!!, obj, main!!, actions))
        }
    }

    /**
     * Удаляет все команды, связанные с выбранным классом.
     *
     * @param cls Класс, который мы удаляем из команд.
     */
    fun unregister(cls: Class<*>) {
        val iterator = commands.values.iterator()

        while(iterator.hasNext()) {
            if(iterator.next().obj.javaClass.equals(cls)) {
                iterator.remove()
            }
        }
    }

    /**
     * Регистрация парсера для конвертации строки в объект.
     *
     * @param clz Класс, который мы будем конвертировать в объект.
     * @param parser Парсер, который знает как парсить класс.
     */
    fun bind(clz: Class<*>, parser: ArgumentParser<*>) {
        parsers.put(clz, parser)
    }

    /**
     * Регистрация дополнителя для автодополнения команды.
     *
     * @param clz Класс, который мы будем автодополнять.
     * @param completer Дополнитель, который знает как дополнять класс.
     */
    fun bind(clz: Class<*>, completer: CommandCompleter) {
        completers.put(clz, completer)
    }
}
