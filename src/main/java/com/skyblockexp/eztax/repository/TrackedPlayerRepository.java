package com.skyblockexp.eztax.repository;

import com.skyblockexp.eztax.storage.TrackedPlayerModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the set of players the tax system has ever encountered.
 *
 * <p>Only available when MySQL storage is configured.
 */
public interface TrackedPlayerRepository {

    /**
     * Insert or update the player record.
     *
     * <ul>
     *   <li>First encounter: creates the row with {@code first_seen_ms} and
     *       {@code last_seen_ms} both set to now.</li>
     *   <li>Subsequent calls: refreshes {@code name} and {@code last_seen_ms},
     *       and adds {@code taxAmount} to the running {@code total_taxed}.</li>
     * </ul>
     *
     * @param playerUuid UUID of the player
     * @param playerName display name (may be null for offline players)
     * @param taxAmount  amount collected in this event (may be 0)
     */
    void upsert(UUID playerUuid, String playerName, double taxAmount);

    /** Find a specific player by UUID. */
    Optional<TrackedPlayerModel> findByUuid(UUID playerUuid);

    /** All tracked players, ordered by {@code total_taxed} descending. */
    List<TrackedPlayerModel> findAll();
}
