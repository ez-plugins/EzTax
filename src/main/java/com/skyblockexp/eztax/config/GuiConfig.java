package com.skyblockexp.eztax.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.Objects;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.skyblockexp.eztax.EzTaxPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public final class GuiConfig {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    public GuiConfig(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        File file = new File(plugin.getDataFolder(), "gui.yml");
        if (!file.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "gui.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Resolve a label from the GUI config. If the configured value starts with "msg:", the
     * remainder is treated as a key into the messages.yml and resolved via the Messages system
     * (returns a legacy-formatted string). Otherwise the raw string is returned.
     */
    public String getLabel(String path) {
        String raw = config.getString(path, "");
        if (raw == null) return "";
        if (raw.startsWith("msg:")) {
            String key = raw.substring(4);
            if (plugin instanceof EzTaxPlugin) {
                EzTaxPlugin ep = (EzTaxPlugin) plugin;
                try {
                    Component comp = ep.getMessages().plain(key);
                    return LegacyComponentSerializer.legacySection().serialize(comp);
                } catch (Exception ignored) {
                    // fallback to raw key if messages missing
                    return key;
                }
            }
            return key;
        }
        return raw;
    }

    /**
     * Resolve a label and replace named placeholders. If the configured value starts with
     * "msg:", the remainder is treated as a messages.yml key and resolved via the Messages
     * system using MiniMessage, with the provided placeholders as TagResolvers.
     * Otherwise performs a simple textual replacement for tokens like <name>.
     * @param path path in gui.yml
     * @param placeholders map of placeholder name -> value
     * @return resolved, legacy-serialized string
     */
    public String getLabel(String path, java.util.Map<String, String> placeholders) {
        String raw = config.getString(path, "");
        if (raw == null) return "";
        if (raw.startsWith("msg:")) {
            String key = raw.substring(4);
            if (plugin instanceof EzTaxPlugin) {
                EzTaxPlugin ep = (EzTaxPlugin) plugin;
                try {
                    java.util.List<TagResolver> resolvers = new java.util.ArrayList<>();
                    if (placeholders != null) {
                        for (java.util.Map.Entry<String, String> e : placeholders.entrySet()) {
                            resolvers.add(Placeholder.parsed(e.getKey(), e.getValue()));
                        }
                    }
                    Component comp = ep.getMessages().message(key, resolvers.toArray(new TagResolver[0]));
                    return LegacyComponentSerializer.legacySection().serialize(comp);
                } catch (Exception ignored) {
                    return key;
                }
            }
            return key;
        }
        // simple replacement for non-msg labels: replace <name> with value
        if (placeholders != null && !placeholders.isEmpty()) {
            for (java.util.Map.Entry<String, String> e : placeholders.entrySet()) {
                raw = raw.replace("<" + e.getKey() + ">", e.getValue());
            }
        }
        return raw;
    }
}
