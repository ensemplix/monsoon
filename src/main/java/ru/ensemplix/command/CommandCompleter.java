package ru.ensemplix.command;

import java.util.Collection;

public interface CommandCompleter {

    Collection<String> complete(String value);

}
