package ru.ensemplix.command.region;

import ru.ensemplix.command.Command;
import ru.ensemplix.command.CommandSender;

public class SimpleRegionCommand {

    public String name;

    @Command
    public void region(CommandSender sender, SimpleRegion region) {
        this.name = region.name;
    }

}
