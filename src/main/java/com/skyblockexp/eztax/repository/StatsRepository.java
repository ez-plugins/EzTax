package com.skyblockexp.eztax.repository;

import com.skyblockexp.eztax.storage.StatsData;

import java.io.IOException;

/**
 * Repository abstraction for stats persistence. Implementations delegate to a StorageProvider.
 */
public interface StatsRepository {
    StatsData load() throws IOException;
    void save(StatsData data) throws IOException;
}
