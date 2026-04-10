package com.skyblockexp.eztax.storage;

import java.io.Closeable;
import java.io.IOException;

/**
 * Generic storage provider for StatsData. Implementations may persist to YML, MySQL, etc.
 */
public interface StorageProvider extends Closeable {
    StatsData load() throws IOException;
    void save(StatsData data) throws IOException;

    @Override
    default void close() throws IOException {
        // optional
    }
}
