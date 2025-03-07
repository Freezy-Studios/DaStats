package me.freezy.plugins.papermc.dastats;

import lombok.Getter;
import me.freezy.plugins.papermc.dastats.config.Stats;
import me.freezy.plugins.papermc.dastats.listener.JoinQuitListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DaStats extends JavaPlugin {
    @Getter
    private static DaStats instance;
    @Getter
    private ConcurrentHashMap<UUID, Stats> playerStats = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        DaStats.instance = this;
        Stats.loadStats();
        saveTimer();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new JoinQuitListener(), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void saveTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Iterate over the playerStats map and save each player's stats
                DaStats.getInstance().getPlayerStats().forEach((uuid, stats) -> {
                    if (stats != null) {
                        stats.save(); // Call the save method for each player's stats
                    } else {
                        getSLF4JLogger().warn("Stats for player {} are null, skipping save.", uuid);
                    }
                });
                getSLF4JLogger().info("Player stats saved successfully.");
            }
        }.runTaskTimerAsynchronously(this, 0L, 5L * 60L * 20L); // Save every 5 minutes
    }

}
