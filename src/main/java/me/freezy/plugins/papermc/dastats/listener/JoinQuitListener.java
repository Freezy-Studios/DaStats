package me.freezy.plugins.papermc.dastats.listener;

import me.freezy.plugins.papermc.dastats.DaStats;
import me.freezy.plugins.papermc.dastats.config.Stats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    public JoinQuitListener() {
        // Start the runnable to increase playtime every tick
        startPlaytimeRunnable();
    }

    private void startPlaytimeRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Iterate over all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerUUID = player.getUniqueId();
                    Stats stats = DaStats.getInstance().getPlayerStats().get(playerUUID);

                    if (stats != null) {
                        // Increase playtime by 50 milliseconds (1 tick = 50 ms)
                        stats.setPlayTime(stats.getPlayTime() + 50);
                    }
                }
            }
        }.runTaskTimer(DaStats.getInstance(), 0L, 1L); // Run every tick
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Create a new Stats object for the player if it doesn't exist
        DaStats.getInstance().getPlayerStats().putIfAbsent(playerUUID, new Stats(playerUUID));

        // Optionally, you can log the join event
        System.out.println(player.getName() + " has joined the server.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Save the player's stats when they quit
        Stats stats = DaStats.getInstance().getPlayerStats().get(playerUUID);
        if (stats != null) {
            stats.save(); // Save the player's stats to file
        }

        // Optionally, you can log the quit event
        System.out.println(player.getName() + " has left the server.");
    }
}
