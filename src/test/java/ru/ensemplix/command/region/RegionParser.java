package ru.ensemplix.command.region;

import ru.ensemplix.command.TypeParser;

public class RegionParser implements TypeParser<Region> {

    @Override
    public Region parse(String value) {
        return new Region(value);
    }

}
