package me.freezy.plugins.papermc.dastats.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.freezy.plugins.papermc.dastats.DaStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Stats {
    private static final Logger LOGGER = LoggerFactory.getLogger(Stats.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Setter @Getter private UUID playerUUID;
    @Setter @Getter private double playTime;

    public Stats(UUID uuid) {
        this.playerUUID = uuid;
        this.playTime = 0.0; // Initialize playTime to 0
    }

    public void save() {
        String statsDirPath = "plugins/dastats/players";
        File statsFile = new File(statsDirPath, getPlayerUUID().toString() + ".json");

        try (FileWriter writer = new FileWriter(statsFile)) {
            GSON.toJson(this, writer);
            LOGGER.info("Successfully saved stats for player: {}", playerUUID);
        } catch (IOException e) {
            LOGGER.error("Failed to save stats for player: {}", playerUUID, e);
        }
    }

    public static void loadStats() {
        Map<UUID, Stats> statsMap = DaStats.getInstance().getPlayerStats();

        String statsDirPath = "plugins/dastats/players";
        File statsDirFile = new File(statsDirPath);

        if (!statsDirFile.exists()) {
            if (statsDirFile.mkdirs()) {
                LOGGER.info("Successfully created directories!");
            } else {
                LOGGER.error("Failed to create directories!");
            }
            return; // Exit if the directory was just created
        }
        if (statsDirFile.listFiles() == null) {
            LOGGER.warn("Stats directory Empty!");
            return;
        }
        for (File file : Objects.requireNonNull(statsDirFile.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                UUID playerUUID = UUID.fromString(file.getName().replace(".json", ""));
                try (FileReader reader = new FileReader(file)) {
                    Stats stats = GSON.fromJson(reader, Stats.class);
                    statsMap.put(playerUUID, stats);
                    LOGGER.info("Loaded stats for player: {}", playerUUID);
                } catch (IOException e) {
                    LOGGER.error("Failed to load stats for player: {}", playerUUID, e);
                }
            }
        }
    }
}
