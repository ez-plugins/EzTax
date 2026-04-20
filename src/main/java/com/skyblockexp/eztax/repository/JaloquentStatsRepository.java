package com.skyblockexp.eztax.repository;

import com.github.ezframework.jaloquent.model.ModelRepository;
import com.github.ezframework.jaloquent.model.TableRegistry;
import com.github.ezframework.jaloquent.store.DataStore;
import com.github.ezframework.javaquerybuilder.query.sql.SqlDialect;
import com.skyblockexp.eztax.service.TaxSink;
import com.skyblockexp.eztax.storage.StatsData;
import com.skyblockexp.eztax.storage.StatsModel;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * {@link StatsRepository} implementation backed by a Jaloquent
 * {@link ModelRepository}.
 *
 * <p>For YAML storage, pass a
 * {@link com.skyblockexp.eztax.storage.BukkitYamlDataStore} as the store.
 * For MySQL storage, register the table via
 * {@link TableRegistry#register(String, String, java.util.Map)} before
 * constructing this repository, then pass a
 * {@link com.skyblockexp.eztax.libs.jaloquent.store.sql.DataSourceJdbcStore}.
 */
public class JaloquentStatsRepository implements StatsRepository {

    private final ModelRepository<StatsModel> modelRepository;

    /**
     * Create a repository using {@link SqlDialect#STANDARD} (suitable for YAML storage).
     *
     * @param store  backing store
     * @param prefix repository prefix
     */
    public JaloquentStatsRepository(DataStore store, String prefix) {
        this(store, prefix, SqlDialect.STANDARD);
    }

    /**
     * Create a repository with an explicit SQL dialect.
     * Use {@link SqlDialect#MYSQL} when the store is a {@link com.github.ezframework.jaloquent.store.sql.DataSourceJdbcStore}
     * backed by MySQL so that identifiers are backtick-quoted.
     *
     * @param store   backing store (YAML or JDBC)
     * @param prefix  repository prefix; for SQL this must match the prefix passed
     *                to {@link TableRegistry#register}
     * @param dialect SQL dialect for query rendering
     */
    public JaloquentStatsRepository(DataStore store, String prefix, SqlDialect dialect) {
        this.modelRepository = new ModelRepository<>(store, prefix, (id, data) -> new StatsModel(id), dialect);
    }

    // -------------------------------------------------------------------------

    @Override
    public StatsData load() throws IOException {
        try {
            Optional<StatsModel> opt = modelRepository.find(StatsModel.MODEL_ID);
            StatsData data = new StatsData();
            if (opt.isPresent()) {
                StatsModel m = opt.get();
                data.setTotalRemoved(m.getTotalRemoved());
                data.setDailyRemoved(m.getDailyRemoved());
                data.setWeeklyRemoved(m.getWeeklyRemoved());
                data.setTreasuryBalance(m.getTreasuryBalance());

                String daily = m.getDailyResetDate();
                if (daily != null && !daily.isEmpty()) {
                    data.setDailyResetDate(LocalDate.parse(daily));
                }
                String weekly = m.getWeeklyResetDate();
                if (weekly != null && !weekly.isEmpty()) {
                    data.setWeeklyResetDate(LocalDate.parse(weekly));
                }

                for (TaxSink sink : TaxSink.values()) {
                    data.getSinkTotals().put(sink, m.getSinkTotal(sink));
                }
            }
            return data;
        } catch (Exception e) {
            throw new IOException("Failed to load stats model via Jaloquent", e);
        }
    }

    @Override
    public void save(StatsData data) throws IOException {
        try {
            StatsModel m = new StatsModel(StatsModel.MODEL_ID);
            m.setTotalRemoved(data.getTotalRemoved());
            m.setDailyRemoved(data.getDailyRemoved());
            m.setWeeklyRemoved(data.getWeeklyRemoved());
            m.setTreasuryBalance(data.getTreasuryBalance());

            if (data.getDailyResetDate() != null) {
                m.setDailyResetDate(data.getDailyResetDate().toString());
            }
            if (data.getWeeklyResetDate() != null) {
                m.setWeeklyResetDate(data.getWeeklyResetDate().toString());
            }

            for (TaxSink sink : TaxSink.values()) {
                m.setSinkTotal(sink, data.getSinkTotals().getOrDefault(sink, 0.0));
            }

            m.save(modelRepository);
        } catch (Exception e) {
            throw new IOException("Failed to save stats model via Jaloquent", e);
        }
    }
}
