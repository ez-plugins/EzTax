package com.skyblockexp.eztax.config;

public enum TaxInterval {
    HOURLY(20L * 60L * 60L),
    DAILY(20L * 60L * 60L * 24L),
    WEEKLY(20L * 60L * 60L * 24L * 7L);

    private final long ticks;

    TaxInterval(long ticks) {
        this.ticks = ticks;
    }

    public long getTicks() {
        return ticks;
    }

    public static TaxInterval fromString(String value) {
        if (value == null) {
            return DAILY;
        }
        for (TaxInterval interval : values()) {
            if (interval.name().equalsIgnoreCase(value)) {
                return interval;
            }
        }
        return DAILY;
    }
}
