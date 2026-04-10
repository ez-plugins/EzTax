package com.skyblockexp.eztax.command;

import org.bukkit.command.CommandSender;
import java.util.List;

/** Simple autocomplete provider for subcommands. */
public interface Autocomplete {
    List<String> complete(CommandSender sender, String[] args);
}
