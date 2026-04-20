package com.skyblockexp.eztax.command.autocomplete;

import com.skyblockexp.eztax.command.Autocomplete;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EzTaxComplete implements Autocomplete {
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> res = new ArrayList<>();
        if (args == null || args.length == 0) return res;
        if (args.length == 1) {
            if (sender.hasPermission("eztax.command.help") || true) res.add("help");
            if (sender.hasPermission("eztax.command.stats")) res.add("stats");
            if (sender.hasPermission("eztax.command.sinks")) res.add("sinks");
            if (sender.hasPermission("eztax.command.reload")) res.add("reload");
            if (sender.hasPermission("eztax.command.runwealthtax")) res.add("runwealthtax");
            if (sender.hasPermission("eztax.command.runtaxpayment")) res.add("runtaxpayment");
            if (sender.hasPermission("eztax.command.transactiontax.view") || sender.hasPermission("eztax.command.transactiontax.manage")) res.add("transactiontax");
            if (sender.hasPermission("eztax.command.settaxrate")) res.add("settaxrate");
            if (sender.hasPermission("eztax.command.exempt")) res.add("exempt");
            if (sender.hasPermission("eztax.command.unexempt")) res.add("unexempt");
            if (sender.hasPermission("eztax.command.exemptions")) res.add("exemptions");
            if (sender.hasPermission("eztax.command.history") || sender.hasPermission("eztax.command.history.others")) res.add("history");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("transactiontax")) {
                if (sender.hasPermission("eztax.command.transactiontax.manage")) {
                    res.add("view");
                    res.add("setpercent");
                    res.add("setmin");
                    res.add("enable");
                    res.add("disable");
                } else {
                    res.add("view");
                }
            } else if (args[0].equalsIgnoreCase("exempt") || args[0].equalsIgnoreCase("unexempt")) {
                // delegate to ExemptComplete for player name suggestions (registered separately)
            }
        }
        return res;
    }
}
