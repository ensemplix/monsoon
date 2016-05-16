package ru.ensemplix.command.region;

import ru.ensemplix.command.argument.Argument;
import ru.ensemplix.command.argument.ArgumentParser;

import static ru.ensemplix.command.argument.Argument.Result.SUCCESS;

public class RegionArgumentParser implements ArgumentParser<Region> {

    @Override
    public Argument<Region> parseArgument(String value) {
        return new Argument<>(SUCCESS, new Region(value));
    }

}
