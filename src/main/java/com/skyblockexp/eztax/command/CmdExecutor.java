package com.skyblockexp.eztax.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * Thin helper to implement combined CommandExecutor + TabCompleter.
 * Top-level command classes can use or extend this when delegating to Subcommand/Autocomplete.
 */
public abstract class CmdExecutor implements CommandExecutor, TabCompleter, Subcommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, args == null ? new String[0] : args);
    }

    @Override
    public abstract boolean execute(CommandSender sender, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args == null) return Collections.emptyList();
        if (args.length == 1) {
            // If a global autocomplete provider is registered, use it first
            Autocomplete global = getAutocomplete("");
            if (global != null) {
                try {
                    List<String> g = global.complete(sender, args == null ? new String[0] : args);
                    if (g != null) return g;
                } catch (Exception ignored) {
                }
            }
            List<String> res = new ArrayList<>();
            for (String s : subcommandNames()) {
                if (s.regionMatches(true, 0, args[0], 0, args[0].length())) {
                    res.add(s);
                }
            }
            return res;
        }
        // Delegate to subcommand-specific autocomplete when available
        Autocomplete ac = getAutocomplete(args[0]);
        if (ac != null) {
            try {
                List<String> list = ac.complete(sender, args);
                return list == null ? Collections.emptyList() : list;
            } catch (Exception ignored) {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    // --- Subcommand registry helpers ---
    private final Map<String, Subcommand> subcommands = new LinkedHashMap<>();
    private final Map<String, Autocomplete> autocompletes = new LinkedHashMap<>();

    /** Register a subcommand name (lowercasing is handled) */
    protected void registerSubcommand(String name, Subcommand sub) {
        if (name == null || sub == null) return;
        subcommands.put(name.toLowerCase(Locale.US), sub);
    }

    /** Register an autocomplete provider for a subcommand. */
    protected void registerAutocomplete(String name, Autocomplete ac) {
        if (name == null || ac == null) return;
        autocompletes.put(name.toLowerCase(Locale.US), ac);
    }

    /** Get registered subcommand names in insertion order. */
    protected List<String> subcommandNames() {
        return new ArrayList<>(subcommands.keySet());
    }

    /** Dispatch to a registered subcommand based on args[0]. Returns the subcommand result, or false if none matched. */
    protected boolean dispatchSubcommand(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) return false;
        String key = args[0] == null ? "" : args[0].toLowerCase(Locale.US);
        Subcommand sub = subcommands.get(key);
        if (sub == null) return false;
        return sub.execute(sender, args);
    }

    /** Lookup an autocomplete provider for the given subcommand name. */
    protected Autocomplete getAutocomplete(String name) {
        if (name == null) return null;
        return autocompletes.get(name.toLowerCase(Locale.US));
    }
}
