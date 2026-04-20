package com.skyblockexp.eztax.repository;

import com.github.ezframework.jaloquent.exception.StorageException;
import com.github.ezframework.jaloquent.model.ModelRepository;
import com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore;
import com.github.ezframework.javaquerybuilder.query.builder.QueryBuilder;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.skyblockexp.eztax.service.TaxSink;
import com.skyblockexp.eztax.storage.TaxHistoryModel;

import java.util.List;
import java.util.UUID;

/**
 * Jaloquent-backed {@link TaxHistoryRepository}.
 *
 * <p>Each call to {@link #record} inserts a new row; no upsert is needed
 * since every event has its own random UUID as the primary key.
 */
public class JaloquentTaxHistoryRepository implements TaxHistoryRepository {

    private final ModelRepository<TaxHistoryModel> modelRepository;

    public JaloquentTaxHistoryRepository(DataSourceJdbcStore store, String prefix, SqlDialect dialect) {
        this.modelRepository = new ModelRepository<>(store, prefix, (id, data) -> new TaxHistoryModel(id), dialect);
    }

    @Override
    public void record(UUID playerUuid, String playerName, TaxSink sink, double amount, double balanceBefore) {
        TaxHistoryModel m = new TaxHistoryModel(UUID.randomUUID().toString());
        m.setPlayerUuid(playerUuid.toString());
        m.setPlayerName(playerName != null ? playerName : "unknown");
        m.setSink(sink.name());
        m.setAmount(amount);
        m.setBalanceBefore(balanceBefore);
        m.setTimestampMs(System.currentTimeMillis());
        try {
            modelRepository.save(m);
        } catch (StorageException e) {
            throw new RuntimeException("Failed to record tax history", e);
        }
    }

    @Override
    public List<TaxHistoryModel> findByPlayer(UUID playerUuid) {
        try {
            return modelRepository.query(
                    new QueryBuilder()
                            .whereEquals("player_uuid", playerUuid.toString())
                            .orderBy("timestamp_ms", false)
                            .build());
        } catch (StorageException e) {
            throw new RuntimeException("Failed to query tax history by player", e);
        }
    }

    @Override
    public List<TaxHistoryModel> findRecent(int limit) {
        try {
            return modelRepository.query(
                    new QueryBuilder()
                            .orderBy("timestamp_ms", false)
                            .limit(limit)
                            .build());
        } catch (StorageException e) {
            throw new RuntimeException("Failed to query recent tax history", e);
        }
    }
}
