package com.skyblockexp.eztax.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class MySqlStorageProviderH2Test {

    @Test
    public void testSaveAndLoadWithH2() throws IOException {
        String url = "jdbc:h2:mem:eztax_test;MODE=MySQL;DB_CLOSE_DELAY=-1";
        DriverManagerDataSource ds = new DriverManagerDataSource(url, "sa", "");
        MySqlStorageProvider provider = new MySqlStorageProvider(ds, "eztax_stats_test");

        StatsData data = provider.load();
        data.setTotalRemoved(123.45);
        data.setDailyRemoved(10.0);
        data.setWeeklyRemoved(20.0);
        data.setTreasuryBalance(500.0);
        data.getSinkTotals().put(com.skyblockexp.eztax.service.TaxSink.TRANSACTION, 1.1);

        provider.save(data);

        StatsData loaded = provider.load();
        assertEquals(123.45, loaded.getTotalRemoved(), 1e-6);
        assertEquals(10.0, loaded.getDailyRemoved(), 1e-6);
        assertEquals(20.0, loaded.getWeeklyRemoved(), 1e-6);
        assertEquals(500.0, loaded.getTreasuryBalance(), 1e-6);
        assertEquals(1.1, loaded.getSinkTotals().getOrDefault(com.skyblockexp.eztax.service.TaxSink.TRANSACTION, 0.0), 1e-6);
    }
}
