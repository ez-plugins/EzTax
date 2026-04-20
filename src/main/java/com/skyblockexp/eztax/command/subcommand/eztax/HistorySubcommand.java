package com.skyblockexp.eztax.command.subcommand.eztax;

import com.skyblockexp.eztax.EzTaxPlugin;
import com.skyblockexp.eztax.MessageManager;
import com.skyblockexp.eztax.command.Subcommand;
import com.skyblockexp.eztax.config.Messages;
import com.skyblockexp.eztax.repository.TaxHistoryRepository;
import com.skyblockexp.eztax.repository.TrackedPlayerRepository;
import com.skyblockexp.eztax.service.TaxEngine;
import com.skyblockexp.eztax.storage.TaxHistoryModel;
import com.skyblockexp.eztax.storage.TrackedPlayerModel;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class HistorySubcommand implements Subcommand {

    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final EzTaxPlugin plugin;
    private final TaxEngine taxEngine;
    private final TaxHistoryRepository taxHistoryRepository;
    private final TrackedPlayerRepository trackedPlayerRepository;
    private final MessageManager messageManager;

    public HistorySubcommand(EzTaxPlugin plugin, TaxEngine taxEngine,
                             TaxHistoryRepository taxHistoryRepository,
                             TrackedPlayerRepository trackedPlayerRepository,
                             Messages messages) {
        this.plugin = plugin;
        this.taxEngine = taxEngine;
        this.taxHistoryRepository = taxHistoryRepository;
        this.trackedPlayerRepository = trackedPlayerRepository;
        this.messageManager = new MessageManager(plugin, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (taxHistoryRepository == null || trackedPlayerRepository == null) {
            messageManager.send(sender, "history-no-database");
            return true;
        }

        OfflinePlayer target;
        boolean selfView;

        if (args != null && args.length >= 2 && !isPageNumber(args[1])) {
            if (!sender.hasPermission("eztax.command.history.others")) {
                messageManager.send(sender, "no-permission");
                return true;
            }
            @SuppressWarnings("deprecation")
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
            if (p == null || (p.getName() == null && !p.isOnline())) {
                messageManager.send(sender, "player-not-found",
                        Placeholder.parsed("player", args[1]));
                return true;
            }
            target = p;
            selfView = false;
        } else if (sender instanceof Player) {
            if (!sender.hasPermission("eztax.command.history")) {
                messageManager.send(sender, "no-permission");
                return true;
            }
            target = (Player) sender;
            selfView = true;
        } else {
            messageManager.send(sender, "history-specify-player");
            return true;
        }

        // Parse optional page number (1-indexed, last arg)
        int page = 1;
        if (args != null && args.length > 1) {
            String last = args[args.length - 1];
            if (isPageNumber(last)) {
                try {
                    page = Math.max(1, Integer.parseInt(last));
                } catch (NumberFormatException ignored) {}
            }
        }

        List<TaxHistoryModel> events = taxHistoryRepository.findByPlayer(target.getUniqueId());
        Optional<TrackedPlayerModel> tracked = trackedPlayerRepository.findByUuid(target.getUniqueId());

        String displayName = target.getName() != null ? target.getName() : target.getUniqueId().toString();

        int totalEvents = events.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalEvents / (double) PAGE_SIZE));
        page = Math.min(page, totalPages);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalEvents);
        List<TaxHistoryModel> pageEvents = events.subList(start, end);

        double totalTaxed = tracked.map(TrackedPlayerModel::getTotalTaxed).orElse(0.0);
        String memberSince = tracked
                .filter(t -> t.getFirstSeenMs() > 0)
                .map(t -> DATE_FMT.format(Instant.ofEpochMilli(t.getFirstSeenMs())))
                .orElse("N/A");
        String lastSeen = tracked
                .filter(t -> t.getLastSeenMs() > 0)
                .map(t -> DATE_FMT.format(Instant.ofEpochMilli(t.getLastSeenMs())))
                .orElse("N/A");

        messageManager.send(sender, "history-header",
                Placeholder.parsed("player", displayName));
        messageManager.send(sender, "history-summary",
                Placeholder.parsed("total", taxEngine.format(totalTaxed)),
                Placeholder.parsed("member_since", memberSince),
                Placeholder.parsed("last_seen", lastSeen));

        if (totalEvents == 0) {
            messageManager.send(sender, "history-empty");
            return true;
        }

        for (TaxHistoryModel event : pageEvents) {
            String date = event.getTimestampMs() > 0
                    ? DATE_FMT.format(Instant.ofEpochMilli(event.getTimestampMs()))
                    : "N/A";
            String sink = event.getSink() != null ? event.getSink() : "?";
            messageManager.send(sender, "history-entry",
                    Placeholder.parsed("date", date),
                    Placeholder.parsed("type", sink),
                    Placeholder.parsed("amount", String.format(Locale.US, "%.2f", event.getAmount())),
                    Placeholder.parsed("balance_before", String.format(Locale.US, "%.2f", event.getBalanceBefore())));
        }

        messageManager.send(sender, "history-footer",
                Placeholder.parsed("page", String.valueOf(page)),
                Placeholder.parsed("total_pages", String.valueOf(totalPages)),
                Placeholder.parsed("total_events", String.valueOf(totalEvents)));

        return true;
    }

    private boolean isPageNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
