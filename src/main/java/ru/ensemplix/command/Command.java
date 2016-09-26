package ru.ensemplix.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * С помощью этой аннотации мы обозначаем команду.
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * Обозначает, что выбранная команда является главной.
     *
     * В случае если не будет найдено альтернатив, будет выполнена
     * указанная команда.
     *
     * @return {@code true}, если команда является главной.
     */
    boolean main() default false;

    /**
     * Требуется ли для доступа к команде проверка прав.
     *
     * В случае если у пользователя нет необходимых прав, то будет брошено
     * исключение {@link ru.ensemplix.command.exception.CommandAccessException}
     * CommandAccessException.
     *
     * @return {@code true}, если требуется проверка прав.
     */
    boolean permission() default false;

}
