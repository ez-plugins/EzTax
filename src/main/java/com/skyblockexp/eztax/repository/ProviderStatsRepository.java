package com.skyblockexp.eztax.repository;

import com.skyblockexp.eztax.storage.StatsData;
import com.skyblockexp.eztax.storage.StorageProvider;

import java.io.IOException;

/**
 * Simple `StatsRepository` implementation that delegates to a `StorageProvider`.
 *
 * This adapter keeps persistence concerns separated from service logic and allows
 * swapping storage implementations (YML, MySQL) without changing callers.
 */
public class ProviderStatsRepository implements StatsRepository {
    private final StorageProvider provider;

    public ProviderStatsRepository(StorageProvider provider) {
        this.provider = provider;
    }

    @Override
    public StatsData load() throws IOException {
        return provider.load();
    }

    @Override
    public void save(StatsData data) throws IOException {
        provider.save(data);
    }
}
