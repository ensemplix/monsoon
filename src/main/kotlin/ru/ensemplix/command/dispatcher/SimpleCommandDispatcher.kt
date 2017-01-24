package ru.ensemplix.command.dispatcher

import ru.ensemplix.command.*
import ru.ensemplix.command.argument.*
import ru.ensemplix.command.exception.CommandAccessException
import ru.ensemplix.command.exception.CommandException
import ru.ensemplix.command.exception.CommandNotFoundException
import ru.ensemplix.command.util.Sentence
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Основной класс для работы с командами.
 */
class SimpleCommandDispatcher : CommandDispatcher {

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
        bind(Int::class.javaObjectType, IntegerArgumentParser())
        bind(Boolean::class.java, BooleanArgumentParser())
        bind(Boolean::class.javaObjectType, BooleanArgumentParser())
        bind(Float::class.java, FloatArgumentParser())
        bind(Float::class.javaObjectType, FloatArgumentParser())
        bind(Double::class.java, StringArgumentParser())
        bind(Double::class.javaObjectType, StringArgumentParser())
        bind(Long::class.java, LongArgumentParser())
        bind(Long::class.javaObjectType, LongArgumentParser())

        // Вспомогательные парсеры.
        bind(Sentence::class.java, SentenceArgumentParser())
    }

    @Throws(CommandException::class)
    override fun call(sender: CommandSender, cmd: String): CommandResult {
        val context = validate(sender, cmd)
        val action = context.action ?: throw CommandNotFoundException()

        val method = action.method
        val args = context.args
        val parameters = method.parameters
        val length = parameters.size
        val arguments = ArrayList<Argument<*>>()
        val parsed = arrayOfNulls<Any>(length)

        // Указатель на текущие положение в переданной строке.
        var currentPosition = 0

        parsed[0] = sender

        for(i in 1..length - 1) {
            val parameterType = parameters[i].type
            val parser: ArgumentParser<*>

            if(Iterable::class.java.isAssignableFrom(parameterType)) {
                val type = parameters[i].parameterizedType as ParameterizedType
                val argumentType = type.actualTypeArguments[0]

                if(argumentType is ParameterizedType && Argument::class.java.isAssignableFrom(argumentType.rawType as Class<*>)) {
                    parser = parsers[argumentType.actualTypeArguments[0]]!!
                } else {
                    parser = parsers[argumentType]!!
                }
            } else if(Argument::class.java.isAssignableFrom(parameterType)) {
                val type = parameters[i].parameterizedType as ParameterizedType
                parser = parsers[type.actualTypeArguments[0]]!!
            } else {
                parser = parsers[parameterType]!!
            }

            if(Iterable::class.java.isAssignableFrom(parameterType)) {
                // Подготоваливаем коллекцию.
                val collection = ArrayList<Any?>()

                for(y in currentPosition..args.size - 1) {
                    val type = parameters[i].parameterizedType as ParameterizedType
                    val argumentType = type.actualTypeArguments[0]
                    val argument = parser.parseArgument(context, y, args[y])
                    currentPosition += argument.consume

                    if(argumentType is ParameterizedType && Argument::class.java.isAssignableFrom(argumentType.rawType as Class<*>)) {
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

                if(args.size > currentPosition) {
                    argument = parser.parseArgument(context, currentPosition, args[currentPosition])

                    if(argument.text == null) {
                        argument.text = args[currentPosition]
                    }

                    currentPosition += argument.consume
                } else {
                    argument = parser.parseArgument(context, currentPosition, null)
                    currentPosition += argument.consume
                }

                if(Argument::class.java.isAssignableFrom(parameterType)) {
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
            val result = method.invoke(context.handler.obj, *parsed)
            val success = result == null || result as Boolean
            return CommandResult(context, arguments, success)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun complete(sender: CommandSender, cmd: String): Collection<String> {
        val context: CommandContext

        try {
            context = validate(sender, cmd)
        } catch (e: CommandException) {
            val matches = ArrayList<String>()
            val names = commands.keys

            if(cmd.isNotEmpty()) {
                names.forEach {
                    if(it.startsWith(cmd)) {
                        matches.add("/$it")
                    }
                }

                return matches
            }

            names.forEach { matches.add("/$it") }
            return matches
        }

        val actions = context.handler.actions.keys
        val action = context.action
        val args = context.args

        if(args.isEmpty() && cmd.last() != ' ') {
            return emptyList<String>()
        }

        if(args.size == 1 && (action == null || context.handler.mains.isNotEmpty())) {
            val matches = ArrayList<String>()

            actions.forEach {
                if(it.startsWith(args[0])) {
                    matches.add(it)
                }
            }

            if(matches.isNotEmpty()) {
                return matches
            }
        }

        if(action == null && context.handler.mains.isEmpty()) {
            return actions
        }

        var arg = ""
        var i = 1

        if(args.isNotEmpty()) {
            i = args.size
            arg = args[i - 1]
        }

        val parameters = context.action!!.method.parameters
        val parameterType = parameters[i].type
        val completer: CommandCompleter?

        if(!Iterable::class.java.isAssignableFrom(parameterType)) {
            val argsLength = if(cmd.last() == ' ') args.size + 1 else args.size

            if(argsLength > context.action.method.parameterCount - 1) {
                return emptyList<String>()
            }
        }

        if(Iterable::class.java.isAssignableFrom(parameterType)) {
            val type = parameters[i].parameterizedType as ParameterizedType
            val argumentType = type.actualTypeArguments[0]

            if(argumentType is ParameterizedType && Argument::class.java.isAssignableFrom(argumentType.rawType as Class<*>)) {
                completer = completers[argumentType.actualTypeArguments[0]]
            } else {
                completer = completers[argumentType]
            }
        } else if(Argument::class.java.isAssignableFrom(parameterType)) {
            val type = parameters[i].parameterizedType as ParameterizedType
            completer = completers[type.actualTypeArguments[0]]
        } else {
            completer = completers[parameterType]
        }

        if(completer != null) {
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
        if(cmd == null || cmd.isEmpty()) {
            throw CommandNotFoundException()
        }

        var args = cmd.toLowerCase().split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        val handler = commands[args[0]]

        if(handler == null) {
            throw CommandNotFoundException()
        }

        val commandActions = handler.actions
        var action: CommandAction? = null
        var main: CommandAction? = null

        if(args.size > 1 && commandActions.containsKey(args[1])) {
            val actions = commandActions[args[1]]!!
            args = Arrays.copyOfRange<String>(args, 2, args.size)
            action = chooseAction(actions, args.size)
        } else {
            args = Arrays.copyOfRange<String>(args, 1, args.size)
            val mains =  handler.mains

            if(mains.isNotEmpty()) {
                action = chooseAction(mains, args.size)
                main = action
            }
        }

        val actionName: String? = if(action != null) action.method.name.toLowerCase() else null

        if(action != null) {
            val method = action.method
            val checkAction = if(main == null || main.method != method) method.name.toLowerCase() else null

            if(action.annotation.permission && !sender.canUseCommand(handler.name, checkAction)) {
                throw CommandAccessException()
            }
        }

        return CommandContext(handler.name, actionName, action, args, handler)
    }

    private fun chooseAction(actions: List<CommandAction?>, argsLength: Int): CommandAction {
        if(actions.size == 1) {
            return actions.first()!!
        }

        var action: CommandAction? = null

        for(possibleAction in actions) {
            if(possibleAction!!.method.parameterCount - 1 >= argsLength) {
                if(action != null && possibleAction.method.parameterCount > action.method.parameterCount) {
                    continue
                }

                action = possibleAction
            }
        }

        return action!!
    }

    override fun register(obj: Any, vararg names: String?) {
        // Проверяем, что команды с таким именем еще нет.
        for(name in names) {
            validateCommandName(name)
        }

        val commandActions = HashMap<String, ArrayList<CommandAction>>()
        val mains = ArrayList<CommandAction>()

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

            val parameters = method.parameters
            val length = parameters.size

            // Первым параметром команды обязательно должен быть ее отправитель.
            if(length == 0 || !CommandSender::class.java.isAssignableFrom(parameters[0].type)) {
                throw IllegalArgumentException("Please provide command sender for " + method.name)
            }

            // Проверяем, что все параметры команды будут отработаны корректно.
            for(i in 1..length - 1)  {
                var parameterType = parameters[i].type

                if(Iterable::class.java.isAssignableFrom(parameterType)) {
                    if(i + 1 != length) {
                        throw IllegalArgumentException("Iterable must be last parameter in " + method.name)
                    }
                } else {
                    if(Argument::class.java.isAssignableFrom(parameterType)) {
                        val type = parameters[i].parameterizedType as ParameterizedType
                        parameterType = type.actualTypeArguments[0] as Class<*>
                    }

                    if(!parsers.containsKey(parameterType)) {
                        throw IllegalArgumentException("Please provide type parser for " + parameterType)
                    }
                }
            }

            val action = CommandAction(method, annotation)

            if(annotation.main) {
                mains.add(action)
            }

            val asCommand = annotation.asCommand.toLowerCase()

            if(asCommand.isNotEmpty()) {
                validateCommandName(asCommand)
                commands[asCommand] = CommandHandler(asCommand, obj, Collections.singletonList(action), Collections.emptyMap())
            }

            for(alias in annotation.aliases) {
                registerAction(alias, action, commandActions)
            }

            registerAction(method.name.toLowerCase(), action, commandActions)
        }

        if(commandActions.isEmpty()) {
            throw IllegalStateException("Not found any method marked with @Command")
        }

        for(name in names) {
            commands[name!!.toLowerCase()] = CommandHandler(names[0]!!.toLowerCase(), obj, mains, commandActions)
        }
    }

    private fun validateCommandName(name: String?) {
        if(name == null || name.isEmpty()) {
            throw IllegalArgumentException("Please provide valid command name")
        }

        if(name.contains(' ')) {
            throw IllegalArgumentException("Please provide command name with no whitespace")
        }

        if(commands.containsKey(name.toLowerCase())) {
            throw IllegalArgumentException("Command with name " + name.toLowerCase() + " already exists")
        }
    }

    private fun registerAction(name: String, action: CommandAction, actions: HashMap<String, ArrayList<CommandAction>>) {
        if(!actions.containsKey(name)) {
            actions.put(name, ArrayList<CommandAction>())
        }

        actions.get(name)!!.add(action)
    }

    override fun unregister(cls: Class<*>) {
        val iterator = commands.values.iterator()

        while(iterator.hasNext()) {
            if(iterator.next().obj.javaClass == cls) {
                iterator.remove()
            }
        }
    }

    override fun bind(clz: Class<*>, parser: ArgumentParser<*>) {
        parsers[clz] = parser
    }

    override fun bind(clz: Class<*>, completer: CommandCompleter) {
        completers[clz] = completer
    }

}
