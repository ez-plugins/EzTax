package com.skyblockexp.eztax.command.autocomplete;

import com.skyblockexp.eztax.command.Autocomplete;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ExemptComplete implements Autocomplete {
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        var res = new ArrayList<String>();
        if (args == null) return res;
        // Require proper permission to suggest player names for exempt/unexempt
        if (!sender.hasPermission("eztax.command.exempt") && !sender.hasPermission("eztax.command.unexempt")) return res;
        if (args.length == 2) {
            for (var p : Bukkit.getOnlinePlayers()) res.add(p.getName());
        }
        return res;
    }
}
