package com.skyblockexp.eztax.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExemptionService {
    private final JavaPlugin plugin;
    private final File exemptionsFile;
    private FileConfiguration exemptionsConfig;
    private final Set<UUID> exemptPlayers;

    public ExemptionService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.exemptionsFile = new File(plugin.getDataFolder(), "exemptions.yml");
        this.exemptPlayers = new HashSet<>();
        load();
    }

    public void load() {
        if (!exemptionsFile.exists()) {
            try {
                exemptionsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create exemptions file: " + e.getMessage());
            }
        }
        exemptionsConfig = YamlConfiguration.loadConfiguration(exemptionsFile);
        exemptPlayers.clear();
        
        List<String> exemptList = exemptionsConfig.getStringList("exempt-players");
        if (exemptList != null) {
            for (String uuidString : exemptList) {
                try {
                    exemptPlayers.add(UUID.fromString(uuidString));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in exemptions: " + uuidString);
                }
            }
        }
    }

    public void save() {
        exemptionsConfig.set("exempt-players", exemptPlayers.stream()
            .map(UUID::toString)
            .toArray(String[]::new));
        try {
            exemptionsConfig.save(exemptionsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save exemptions: " + e.getMessage());
        }
    }

    public boolean isExempt(UUID playerId) {
        return exemptPlayers.contains(playerId);
    }

    public boolean isExempt(Player player) {
        return isExempt(player.getUniqueId());
    }

    public void addExemption(UUID playerId) {
        exemptPlayers.add(playerId);
        save();
    }

    public void addExemption(Player player) {
        addExemption(player.getUniqueId());
    }

    public boolean removeExemption(UUID playerId) {
        boolean removed = exemptPlayers.remove(playerId);
        if (removed) {
            save();
        }
        return removed;
    }

    public boolean removeExemption(Player player) {
        return removeExemption(player.getUniqueId());
    }

    public Set<UUID> getExemptPlayers() {
        return new HashSet<>(exemptPlayers);
    }

    public int getExemptCount() {
        return exemptPlayers.size();
    }
}
