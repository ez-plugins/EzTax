package com.skyblockexp.eztax.repository;

import com.skyblockexp.eztax.service.TaxSink;
import com.skyblockexp.eztax.storage.TaxHistoryModel;

import java.util.List;
import java.util.UUID;

/**
 * Repository for per-event tax history.
 *
 * <p>Only available when MySQL storage is configured. The YAML storage path
 * does not record per-event history.
 */
public interface TaxHistoryRepository {

    /**
     * Record a tax withdrawal event.
     *
     * @param playerUuid   UUID of the taxed player
     * @param playerName   display name at time of event (may be null)
     * @param sink         tax sink that collected the money
     * @param amount       amount withdrawn
     * @param balanceBefore player balance immediately before the withdrawal
     */
    void record(UUID playerUuid, String playerName, TaxSink sink, double amount, double balanceBefore);

    /** All recorded events for a specific player, newest first. */
    List<TaxHistoryModel> findByPlayer(UUID playerUuid);

    /** The {@code limit} most recent events across all players. */
    List<TaxHistoryModel> findRecent(int limit);
}
