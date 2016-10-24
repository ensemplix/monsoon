package ru.ensemplix.command.region;

import ru.ensemplix.command.Command;
import ru.ensemplix.command.CommandSender;
import ru.ensemplix.command.argument.Argument;

import java.util.List;

public class RegionCommand {

    public String name;
    public List<Argument<Region>> list;

    @Command(main = true)
    public void region(CommandSender sender, Region region) {
        this.name = region.name;
    }

    @Command
    public void remove(CommandSender sender, Argument<Region> region) {

    }

    @Command
    public void list(CommandSender sender, List<Argument<Region>> list) {
        this.list = list;
    }

}
