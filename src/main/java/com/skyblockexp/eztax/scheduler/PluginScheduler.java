package com.skyblockexp.eztax.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

/**
 * Platform-aware task scheduler.
 * Delegates to Folia's threaded-region schedulers when running on Folia,
 * and falls back to the standard Bukkit scheduler on Paper and Spigot.
 *
 * <p>Detection is performed once at class-initialisation time by probing for
 * {@code io.papermc.paper.threadedregions.RegionizedServer}, which is present
 * only in Folia builds.</p>
 */
public final class PluginScheduler {

    /** {@code true} when the server is a Folia build. */
    public static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
            // running on Paper or Spigot — use Bukkit scheduler
        }
        FOLIA = folia;
    }

    private final JavaPlugin plugin;

    public PluginScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Run {@code task} asynchronously once, as soon as possible.
     * Equivalent to {@code Bukkit.getScheduler().runTaskAsynchronously(...)}.
     */
    public void runAsync(Runnable task) {
        if (FOLIA) {
            plugin.getServer().getAsyncScheduler()
                    .runNow(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Run {@code task} on the primary thread (Bukkit/Spigot) or the global
     * region thread (Folia).
     * Equivalent to {@code Bukkit.getScheduler().runTask(...)}.
     */
    public void runSync(Runnable task) {
        if (FOLIA) {
            plugin.getServer().getGlobalRegionScheduler()
                    .run(plugin, t -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Schedule {@code task} to run asynchronously at a fixed rate.
     * Returns a {@link Runnable} that, when invoked, cancels the repeating task.
     *
     * @param delayTicks  initial delay in server ticks (1 tick = 50 ms at 20 TPS)
     * @param periodTicks period between executions in server ticks
     * @return canceller — call {@code canceller.run()} to stop the task
     */
    public Runnable runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        if (FOLIA) {
            long delayMs  = Math.max(1L, delayTicks  * 50L);
            long periodMs = Math.max(1L, periodTicks * 50L);
            var scheduled = plugin.getServer().getAsyncScheduler()
                    .runAtFixedRate(plugin, t -> task.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
            return scheduled::cancel;
        } else {
            var scheduled = Bukkit.getScheduler()
                    .runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
            return scheduled::cancel;
        }
    }
}
