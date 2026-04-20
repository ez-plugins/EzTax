package com.skyblockexp.eztax.integration;

import com.skyblockexp.eztax.service.StatsService;
import com.skyblockexp.lifesteal.seasons.api.SeasonsApi;
import com.skyblockexp.lifesteal.seasons.api.SeasonsIntegration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class EzSeasonsIntegration implements SeasonsIntegration {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final StatsService statsService;
    private final Logger logger;

    public EzSeasonsIntegration(StatsService statsService, Logger logger) {
        this.statsService = statsService;
        this.logger = logger;
    }

    @Override
    public void onRegister(SeasonsApi api) {
        // intentionally silent; EzSeasonsHook already logs the integration-enabled message
    }

    @Override
    public void onUnregister() {
        logger.info("EzSeasons integration removed.");
    }

    @Override
    public void onSeasonReset(long previousResetMillis, long resetMillis, long nextResetMillis, String reason) {
        double totalCollected = statsService.getTotalRemoved();
        double weeklyCollected = statsService.getWeeklyRemoved();

        String prevDate = previousResetMillis > 0
                ? DATE_FMT.format(Instant.ofEpochMilli(previousResetMillis))
                : "N/A";
        String nextDate = nextResetMillis > 0
                ? DATE_FMT.format(Instant.ofEpochMilli(nextResetMillis))
                : "unscheduled";

        logger.info("══════════════════════════════════════════════");
        logger.info("  EzTax — Season Reset Summary");
        logger.info("══════════════════════════════════════════════");
        logger.info("  Season from:     " + prevDate);
        logger.info("  Reset at:        " + DATE_FMT.format(Instant.ofEpochMilli(resetMillis)));
        logger.info("  Next season:     " + nextDate);
        logger.info("  Reason:          " + reason);
        logger.info("  Taxes this week: " + String.format("%.2f", weeklyCollected));
        logger.info("  All-time taxes:  " + String.format("%.2f", totalCollected));
        logger.info("══════════════════════════════════════════════");
    }
}
