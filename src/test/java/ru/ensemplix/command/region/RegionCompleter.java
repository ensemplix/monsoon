package ru.ensemplix.command.region;

import com.google.common.collect.ImmutableSet;
import ru.ensemplix.command.CommandCompleter;
import ru.ensemplix.command.CommandContext;

import java.util.Collection;
import java.util.stream.Collectors;

public class RegionCompleter implements CommandCompleter {

    private static final ImmutableSet<String> regions = ImmutableSet.of("home", "spawn", "spawn123", "spb");

    @Override
    public Collection<String> complete(CommandContext context, String arg) {
        return regions.stream().filter(name -> name.startsWith(arg)).collect(Collectors.toList());
    }

}
