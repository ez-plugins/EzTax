package com.skyblockexp.eztax.command.autocomplete;

import com.skyblockexp.eztax.command.Autocomplete;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TransactionTaxComplete implements Autocomplete {
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> res = new ArrayList<>();
        if (args == null) return res;
        if (args.length == 2) {
            if (sender.hasPermission("eztax.command.transactiontax.manage")) {
                res.add("view");
                res.add("setpercent");
                res.add("setmin");
                res.add("enable");
                res.add("disable");
            } else {
                res.add("view");
            }
        }
        return res;
    }
}
