package com.azael.tempestbridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TempestBridgeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("tempestbridge.json");

    public int messageAuthorColor = 11;
    public int discordTagColor = 14;
    public String pingName = "";
    public String discordTagText = "DISCORD";
    public int rankTagColor = 6;
    public boolean shouldShowEvent = true;
    public boolean shouldEventPing = false;
    public int eventTagColor = 6;
    public int eventTextColor = 11;
    public int eventTimeColor = 15;
    public boolean discordToggle = true;
    public int commandSymbol = 0;
    public int commandTextColor = 11;
    public int commandValueColor = 14;
    public boolean debugLogging = false;
    public List<String> bridgeAccounts = new ArrayList<>(List.of("MrTheAFK", "lfForagingUpdate"));
    public List<String> ignores = new ArrayList<>();

    public static TempestBridgeConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                TempestBridgeConfig config = GSON.fromJson(reader, TempestBridgeConfig.class);
                if (config != null) {
                    if (config.bridgeAccounts == null || config.bridgeAccounts.isEmpty()) config.bridgeAccounts = new ArrayList<>(List.of("MrTheAFK", "lfForagingUpdate"));
                    if (config.ignores == null) config.ignores = new ArrayList<>();
                    return config;
                }
            } catch (Exception e) {
                TempestBridgeClient.LOGGER.warn("Failed to load TempestBridge config, using defaults", e);
            }
        }
        TempestBridgeConfig config = new TempestBridgeConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            TempestBridgeClient.LOGGER.warn("Failed to save TempestBridge config", e);
        }
    }
}
