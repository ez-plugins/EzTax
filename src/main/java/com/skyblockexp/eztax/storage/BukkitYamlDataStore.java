package com.skyblockexp.eztax.storage;

import com.github.ezframework.jaloquent.store.DataStore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jaloquent {@link DataStore} implementation backed by a Bukkit YAML file.
 *
 * <p>Storage paths use {@code '/'} as a separator (the Jaloquent convention).
 * These are converted to nested YAML keys by replacing {@code '/'} with {@code '.'}.
 * For example, the path {@code "eztax_stats/stats"} is stored under the YAML key
 * {@code eztax_stats.stats}.
 *
 * <p>Every write is immediately persisted to disk.
 */
public class BukkitYamlDataStore implements DataStore {

    private final File file;
    private final Logger logger;
    private YamlConfiguration yaml;

    public BukkitYamlDataStore(File file, Logger logger) {
        this.file = file;
        this.logger = logger;
        this.yaml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
    }

    @Override
    public void save(String path, Map<String, Object> data) {
        String key = toYamlKey(path);
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            yaml.set(key + "." + entry.getKey(), entry.getValue());
        }
        persist();
    }

    @Override
    public Optional<Map<String, Object>> load(String path) {
        ConfigurationSection section = yaml.getConfigurationSection(toYamlKey(path));
        if (section == null) {
            return Optional.empty();
        }
        Map<String, Object> data = new LinkedHashMap<>();
        for (String k : section.getKeys(false)) {
            data.put(k, section.get(k));
        }
        return Optional.of(data);
    }

    @Override
    public void delete(String path) {
        yaml.set(toYamlKey(path), null);
        persist();
    }

    @Override
    public boolean exists(String path) {
        return yaml.contains(toYamlKey(path));
    }

    // -------------------------------------------------------------------------

    private String toYamlKey(String path) {
        return path.replace('/', '.');
    }

    private void persist() {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            yaml.save(file);
        } catch (IOException e) {
            if (logger != null) {
                logger.log(Level.WARNING, "BukkitYamlDataStore: failed to save " + file.getName(), e);
            }
        }
    }
}
