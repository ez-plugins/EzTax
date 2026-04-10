package com.skyblockexp.eztax;

import com.skyblockexp.eztax.config.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageManager {
    private final Messages messages;
    private final Plugin plugin;

    public MessageManager(Plugin plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    /**
     * Sends a message to a CommandSender, using MiniMessage if possible, otherwise legacy color codes.
     * Handles both Player and Console senders.
     * @param sender The CommandSender
     * @param key The message key in messages.yml
     * @param placeholders Placeholders to replace in the message
     */

    public void send(CommandSender sender, String key, TagResolver... placeholders) {
        Component component = messages.message(key, placeholders);
        // Always serialize to legacy for compatibility (1.7+)
        String legacy = LegacyComponentSerializer.legacySection().serialize(component);
        sender.sendMessage(legacy);
    }

    /**
     * Broadcasts a message to all players on the server.
     * @param key The message key in messages.yml
     * @param placeholders Placeholders to replace in the message
     */
    public void broadcast(String key, TagResolver... placeholders) {
        Component component = messages.message(key, placeholders);
        String legacy = LegacyComponentSerializer.legacySection().serialize(component);
        plugin.getServer().broadcastMessage(legacy);
    }
}
