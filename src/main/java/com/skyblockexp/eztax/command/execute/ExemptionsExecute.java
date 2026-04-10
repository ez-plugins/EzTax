package com.skyblockexp.eztax.command.execute;

import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.CmdExecutor;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.ExemptionService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class ExemptionsExecute extends CmdExecutor {
    private final ExemptionService exemptionService;
    private final MessageManager messageManager;

    public ExemptionsExecute(org.bukkit.plugin.Plugin plugin, ExemptionService exemptionService, Messages messages) {
        this.exemptionService = exemptionService;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (exemptionService.getExemptCount() == 0) {
            messageManager.send(sender, "no-exemptions");
            return true;
        }

        messageManager.send(sender, "exemptions-header",
            Placeholder.parsed("count", String.valueOf(exemptionService.getExemptCount())));

        for (java.util.UUID uuid : exemptionService.getExemptPlayers()) {
            org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() != null ? player.getName() : uuid.toString();
            messageManager.send(sender, "exemptions-line", Placeholder.parsed("player", name));
        }
        return true;
    }
}
