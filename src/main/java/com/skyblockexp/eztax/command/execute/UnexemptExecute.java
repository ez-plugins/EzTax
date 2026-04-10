package com.skyblockexp.eztax.command.execute;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.CmdExecutor;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.service.ExemptionService;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class UnexemptExecute extends CmdExecutor {
    private final ExemptionService exemptionService;
    private final MessageManager messageManager;

    public UnexemptExecute(EzTaxPlugin plugin, ExemptionService exemptionService, Messages messages) {
        this.exemptionService = exemptionService;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args == null || args.length < 2) {
            messageManager.send(sender, "unexempt-usage");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        if (!exemptionService.isExempt(player.getUniqueId())) {
            messageManager.send(sender, "not-exempt", Placeholder.parsed("player", player.getName()));
            return true;
        }

        exemptionService.removeExemption(player.getUniqueId());
        messageManager.send(sender, "unexempt-success", Placeholder.parsed("player", player.getName()));
        return true;
    }
}
