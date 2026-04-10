package com.skyblockexp.eztax.command.autocomplete;

import com.skyblockexp.eztax.command.Autocomplete;
import com.skyblockexp.eztax.config.TaxConfig;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetTaxRateComplete implements Autocomplete {
    private final TaxConfig config;

    public SetTaxRateComplete(TaxConfig config) {
        this.config = config;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> res = new ArrayList<>();
        if (args == null) return res;
        if (!sender.hasPermission("eztax.command.settaxrate")) return res;
        if (args.length == 2) {
            // Suggest existing group names if group taxes are enabled
            if (config.isGroupTaxesEnabled()) {
                try {
                    var section = config.getPlugin().getConfig().getConfigurationSection("group-taxes.groups");
                    if (section != null) {
                        for (String key : section.getKeys(false)) {
                            res.add(key);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return res;
    }
}
