package com.skyblockexp.eztax.command;

import org.bukkit.command.CommandSender;

/**
 * Represents a single subcommand action.
 */
public interface Subcommand {
    /** Execute the subcommand with the provided args. */
    boolean execute(CommandSender sender, String[] args);
}
