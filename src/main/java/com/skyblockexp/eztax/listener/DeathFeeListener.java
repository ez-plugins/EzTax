package com.skyblockexp.eztax.listener;

import com.skyblockexp.eztax.config.TaxConfig;
import com.skyblockexp.eztax.service.TaxEngine;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathFeeListener implements Listener {
    private final TaxConfig config;
    private final TaxEngine taxEngine;

    public DeathFeeListener(TaxConfig config, TaxEngine taxEngine) {
        this.config = config;
        this.taxEngine = taxEngine;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!config.isDeathFeeEnabled()) {
            return;
        }
        taxEngine.applyDeathFee(event.getEntity());
    }
}
