package ru.ensemplix.command.region;

import ru.ensemplix.command.CommandArgumentParser;

public class RegionArgumentParser implements CommandArgumentParser<Region> {

    @Override
    public Region parseArgument(String value) {
        return new Region(value);
    }

}
