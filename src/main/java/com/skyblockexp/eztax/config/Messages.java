package com.skyblockexp.eztax.config;

import java.io.File;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Messages {
    private final JavaPlugin plugin;
    private FileConfiguration configuration;
    private String prefix;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public Messages(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        // Ensure messages.yml exists
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        configuration = YamlConfiguration.loadConfiguration(file);
        prefix = configuration.getString("prefix", "");
        // Ensure prefix is never null
        if (prefix == null) {
            prefix = "";
        }
    }

    public Component message(String key, TagResolver... resolvers) {
        String raw = configuration.getString(key, "");
        // Check if message contains {prefix} placeholder
        boolean hasPrefix = raw.contains("{prefix}");
        // Replace {prefix} placeholder with actual prefix value if present in message
        if (hasPrefix) {
            raw = raw.replace("{prefix}", prefix);
        }
        if (raw.contains("<")) {
            try {
                // Only prepend prefix if message didn't already have {prefix} placeholder
                String messageWithPrefix = hasPrefix ? raw : prefix + raw;
                return miniMessage.deserialize(messageWithPrefix, resolvers);
            } catch (Exception e) {
                // fallback to legacy
            }
        }
        // Only prepend prefix if message didn't already have {prefix} placeholder
        String messageWithPrefix = hasPrefix ? raw : prefix + raw;
        return legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', messageWithPrefix));
    }

    public Component plain(String key, TagResolver... resolvers) {
        String raw = configuration.getString(key, "");
        // Replace {prefix} placeholder with actual prefix value (for plain messages without auto-prefix)
        if (raw.contains("{prefix}")) {
            raw = raw.replace("{prefix}", prefix);
        }
        if (raw.contains("<")) {
            try {
                return miniMessage.deserialize(raw, resolvers);
            } catch (Exception e) {
                // fallback to legacy
            }
        }
        return legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', raw));
    }
}
