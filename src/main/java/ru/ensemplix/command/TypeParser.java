package ru.ensemplix.command;

public interface TypeParser<T> {

    T parse(String value);

}
