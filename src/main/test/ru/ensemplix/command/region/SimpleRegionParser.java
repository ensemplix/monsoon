package ru.ensemplix.command.region;

import ru.ensemplix.command.TypeParser;

public class SimpleRegionParser implements TypeParser<SimpleRegion> {

    @Override
    public SimpleRegion parse(String value) {
        return new SimpleRegion(value);
    }

}
