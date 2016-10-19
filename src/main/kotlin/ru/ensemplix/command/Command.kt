package ru.ensemplix.command

import kotlin.annotation.AnnotationTarget.*

/**
 * С помощью этой аннотации мы обозначаем команду.
 */
@Target(FUNCTION)
annotation class Command(

        /**
        * Обозначает, что выбранная команда является главной.
        *
        * В случае если не будет найдено альтернатив, будет выполнена
        * указанная команда.
        *
        * @return {@code true}, если команда является главной.
        */
        val main: Boolean = false,

        /**
         * Требуется ли для доступа к команде проверка прав.
         *
         * В случае если у пользователя нет необходимых прав, то будет брошено
         * исключение {@link ru.ensemplix.command.exception.CommandAccessException}
         * CommandAccessException.
         *
         * @return {@code true}, если требуется проверка прав.
         */
         val permission: Boolean = false

)
