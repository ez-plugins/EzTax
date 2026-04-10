package com.skyblockexp.eztax.storage;

import com.skyblockexp.eztax.service.TaxSink;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

/**
 * Lightweight DTO representing persisted stats.
 */
public class StatsData {
    private final Map<TaxSink, Double> sinkTotals = new EnumMap<>(TaxSink.class);
    private double totalRemoved;
    private double dailyRemoved;
    private double weeklyRemoved;
    private double treasuryBalance;
    private LocalDate dailyResetDate;
    private LocalDate weeklyResetDate;

    public Map<TaxSink, Double> getSinkTotals() {
        return sinkTotals;
    }

    public double getTotalRemoved() {
        return totalRemoved;
    }

    public void setTotalRemoved(double totalRemoved) {
        this.totalRemoved = totalRemoved;
    }

    public double getDailyRemoved() {
        return dailyRemoved;
    }

    public void setDailyRemoved(double dailyRemoved) {
        this.dailyRemoved = dailyRemoved;
    }

    public double getWeeklyRemoved() {
        return weeklyRemoved;
    }

    public void setWeeklyRemoved(double weeklyRemoved) {
        this.weeklyRemoved = weeklyRemoved;
    }

    public double getTreasuryBalance() {
        return treasuryBalance;
    }

    public void setTreasuryBalance(double treasuryBalance) {
        this.treasuryBalance = treasuryBalance;
    }

    public LocalDate getDailyResetDate() {
        return dailyResetDate;
    }

    public void setDailyResetDate(LocalDate dailyResetDate) {
        this.dailyResetDate = dailyResetDate;
    }

    public LocalDate getWeeklyResetDate() {
        return weeklyResetDate;
    }

    public void setWeeklyResetDate(LocalDate weeklyResetDate) {
        this.weeklyResetDate = weeklyResetDate;
    }
}
