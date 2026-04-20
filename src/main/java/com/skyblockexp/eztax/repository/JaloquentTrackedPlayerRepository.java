package com.skyblockexp.eztax.repository;

import com.github.ezframework.jaloquent.exception.StorageException;
import com.github.ezframework.jaloquent.model.ModelRepository;
import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.skyblockexp.eztax.storage.TrackedPlayerModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Jaloquent-backed {@link TrackedPlayerRepository}.
 *
 * <p>{@link #upsert} performs a read-then-write so that {@code total_taxed}
 * is incremented correctly rather than overwritten.
 */
public class JaloquentTrackedPlayerRepository implements TrackedPlayerRepository {

    private final ModelRepository<TrackedPlayerModel> modelRepository;

    public JaloquentTrackedPlayerRepository(DataSourceJdbcStore store, String prefix, SqlDialect dialect) {
        this.modelRepository = new ModelRepository<>(store, prefix, (id, data) -> new TrackedPlayerModel(id), dialect);
    }

    @Override
    public void upsert(UUID playerUuid, String playerName, double taxAmount) {
        String id  = playerUuid.toString();
        long   now = System.currentTimeMillis();
        try {
            Optional<TrackedPlayerModel> existing = modelRepository.find(id);
            TrackedPlayerModel m;
            if (existing.isPresent()) {
                m = existing.get();
                if (playerName != null) {
                    m.setName(playerName);
                }
                m.setLastSeenMs(now);
                m.setTotalTaxed(m.getTotalTaxed() + taxAmount);
            } else {
                m = new TrackedPlayerModel(id);
                m.setName(playerName != null ? playerName : "unknown");
                m.setFirstSeenMs(now);
                m.setLastSeenMs(now);
                m.setTotalTaxed(taxAmount);
            }
            modelRepository.save(m);
        } catch (StorageException e) {
            throw new RuntimeException("Failed to upsert tracked player " + id, e);
        }
    }

    @Override
    public Optional<TrackedPlayerModel> findByUuid(UUID playerUuid) {
        try {
            return modelRepository.find(playerUuid.toString());
        } catch (StorageException e) {
            throw new RuntimeException("Failed to find tracked player " + playerUuid, e);
        }
    }

    @Override
    public List<TrackedPlayerModel> findAll() {
        try {
            return modelRepository.query(
                    new QueryBuilder()
                            .orderBy("total_taxed", false)
                            .build());
        } catch (StorageException e) {
            throw new RuntimeException("Failed to query tracked players", e);
        }
    }
}
