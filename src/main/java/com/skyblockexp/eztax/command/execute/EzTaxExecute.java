package com.skyblockexp.eztax.command.execute;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.economy.VaultHook;
import com.skyblockexp.eztax.service.ExemptionService;
import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.autocomplete.EzTaxComplete;
import com.skyblockexp.eztax.command.autocomplete.ExemptComplete;
import com.skyblockexp.eztax.command.autocomplete.TransactionTaxComplete;
import com.skyblockexp.eztax.command.autocomplete.SetTaxRateComplete;
import com.skyblockexp.eztax.command.execute.SetTaxRateExecute;
import com.skyblockexp.eztax.command.execute.TransactionTaxExecute;
import com.skyblockexp.eztax.command.subcommand.eztax.ReloadSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.CheckSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.PaySubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.ConfigGuiSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.PayFineSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.ShowTaxSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.ShowFinesSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.StatsSubcommand;
import com.skyblockexp.eztax.command.subcommand.eztax.SinksSubcommand;
import com.skyblockexp.eztax.command.subcommand.exempt.ExemptSubcommand;
import com.skyblockexp.eztax.command.subcommand.exempt.UnexemptSubcommand;
import com.skyblockexp.eztax.command.subcommand.exempt.ExemptionsSubcommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import com.skyblockexp.eztax.task.WealthTaxTask;
import com.skyblockexp.eztax.task.TaxPaymentTask;

import java.util.Locale;

public class EzTaxExecute extends com.skyblockexp.eztax.command.CmdExecutor {
    private final EzTaxPlugin plugin;
    private final TaxConfig config;
    private final StatsService statsService;
    private final TaxEngine taxEngine;
    private final VaultHook vaultHook;
    private final Messages messages;
    private final MessageManager messageManager;
    private final ExemptionService exemptionService;

    private final SetTaxRateExecute setTaxRateCmd;
    private final TransactionTaxExecute transactionTaxCmd;

    public EzTaxExecute(EzTaxPlugin plugin, TaxConfig config, StatsService statsService, TaxEngine taxEngine, VaultHook vaultHook, Messages messages, ExemptionService exemptionService) {
        this.plugin = plugin;
        this.config = config;
        this.statsService = statsService;
        this.taxEngine = taxEngine;
        this.vaultHook = vaultHook;
        this.messages = messages;
        this.messageManager = new MessageManager(plugin, messages);
        this.exemptionService = exemptionService;

        this.setTaxRateCmd = new SetTaxRateExecute(plugin, config, messages);
        this.transactionTaxCmd = new TransactionTaxExecute(plugin, config, messages);

        // Register subcommands and autocompletes
        registerSubcommand("help", (s, a) -> { sendHelp(s); return true; });
        registerSubcommand("runwealthtax", (s, a) -> {
            if (!s.hasPermission("eztax.command.runwealthtax")) {
                messageManager.send(s, "no-permission");
                return true;
            }
            messageManager.send(s, "wealth-tax-running");
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new WealthTaxTask(plugin, taxEngine, messageManager, s));
            return true;
        });

        registerSubcommand("runtaxpayment", (s, a) -> {
            if (!s.hasPermission("eztax.command.runtaxpayment")) {
                messageManager.send(s, "no-permission");
                return true;
            }
            messageManager.send(s, "run-taxpayments-started");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new TaxPaymentTask(plugin, taxEngine, messageManager, s));
            return true;
        });

        registerSubcommand("reload", new ReloadSubcommand(plugin, messages));
        registerSubcommand("settaxrate", setTaxRateCmd);
        registerSubcommand("check", new CheckSubcommand(plugin, taxEngine, messages));
        registerSubcommand("pay", new PaySubcommand(plugin, taxEngine, messages, plugin.getGuiConfig()));
        registerSubcommand("config", new ConfigGuiSubcommand(plugin));
        registerSubcommand("payfine", new PayFineSubcommand(plugin, taxEngine, messages));
        registerSubcommand("show", new ShowTaxSubcommand(plugin, taxEngine, messages));
        registerSubcommand("fines", new ShowFinesSubcommand(plugin, taxEngine, messages));
        registerSubcommand("stats", new StatsSubcommand(plugin, statsService, messages));
        registerSubcommand("sinks", new SinksSubcommand(plugin, statsService, messages));
        registerSubcommand("transactiontax", transactionTaxCmd);
        registerSubcommand("exempt", new ExemptSubcommand(plugin, exemptionService, messages));
        registerSubcommand("unexempt", new UnexemptSubcommand(plugin, exemptionService, messages));
        registerSubcommand("exemptions", new ExemptionsSubcommand(plugin, exemptionService, messages));

        registerAutocomplete("", new EzTaxComplete());
        registerAutocomplete("exempt", new ExemptComplete());
        registerAutocomplete("transactiontax", new TransactionTaxComplete());
        registerAutocomplete("settaxrate", new SetTaxRateComplete(config));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args == null || args.length == 0) {
            sendHelp(sender);
            return true;
        }
        if (!dispatchSubcommand(sender, args)) {
            sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        messageManager.send(sender, "help-header");
        messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax stats"), Placeholder.parsed("description", "View tax totals"));
        messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax sinks"), Placeholder.parsed("description", "View tax breakdowns"));
        messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax reload"), Placeholder.parsed("description", "Reload configuration"));
        messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax runwealthtax"), Placeholder.parsed("description", "Run wealth tax manually"));
        if (messageManager != null) {
            if (sender.hasPermission("eztax.command.settaxrate")
                || sender.hasPermission("eztax.command.exempt")
                || sender.hasPermission("eztax.command.unexempt")
                || sender.hasPermission("eztax.command.exemptions")
                || sender.hasPermission("eztax.command.transactiontax.manage")
                || sender.hasPermission("eztax.command.transactiontax.view")
                || sender.hasPermission("eztax.command.reload")
                || sender.hasPermission("eztax.command.runwealthtax")
                || sender.hasPermission("eztax.command.runtaxpayment")) {
                messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax settaxrate <rate> [group]"), Placeholder.parsed("description", "Set tax rate"));
                messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax exempt <player>"), Placeholder.parsed("description", "Exempt player from taxes"));
                messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax unexempt <player>"), Placeholder.parsed("description", "Remove player exemption"));
                messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax exemptions"), Placeholder.parsed("description", "List exempt players"));
                messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax transactiontax <view|setpercent|setmin|enable|disable>"), Placeholder.parsed("description", "Configure transaction tax"));
                if (sender.hasPermission("eztax.command.config")) {
                    messageManager.send(sender, "help-line", Placeholder.parsed("command", "/eztax config"), Placeholder.parsed("description", "Open admin configuration GUI"));
                }
            }
        }
    }

}
