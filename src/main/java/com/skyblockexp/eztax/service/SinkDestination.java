package com.skyblockexp.eztax.service;

/**
 * Destination for collected tax money.
 *
 * <ul>
 *   <li>{@link #BURN}    — money is destroyed (reduces economy supply, the default)</li>
 *   <li>{@link #PLAYER}  — money is deposited into a specific player's account</li>
 *   <li>{@link #POOL}    — money is split equally among all currently online players</li>
 *   <li>{@link #COMMAND} — a console command is executed with {@code %amount%} replaced</li>
 * </ul>
 */
public enum SinkDestination {
    BURN,
    PLAYER,
    POOL,
    COMMAND;

    public static SinkDestination fromString(String value) {
        if (value == null) {
            return BURN;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return BURN;
        }
    }
}
