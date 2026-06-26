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
                    config.sanitize();
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

    void sanitize() {
        messageAuthorColor = clamp(messageAuthorColor, 0, TempestBridgeClient.COLORS.length - 1);
        discordTagColor = clamp(discordTagColor, 0, TempestBridgeClient.COLORS.length - 1);
        rankTagColor = clamp(rankTagColor, 0, TempestBridgeClient.COLORS.length - 1);
        eventTagColor = clamp(eventTagColor, 0, TempestBridgeClient.COLORS.length - 1);
        eventTextColor = clamp(eventTextColor, 0, TempestBridgeClient.COLORS.length - 1);
        eventTimeColor = clamp(eventTimeColor, 0, TempestBridgeClient.COLORS.length - 1);
        commandTextColor = clamp(commandTextColor, 0, TempestBridgeClient.COLORS.length - 1);
        commandValueColor = clamp(commandValueColor, 0, TempestBridgeClient.COLORS.length - 1);
        commandSymbol = clamp(commandSymbol, 0, TempestBridgeClient.SYMBOLS.length - 1);

        if (pingName == null) pingName = "";
        if (discordTagText == null || discordTagText.isBlank()) discordTagText = "DISCORD";
        bridgeAccounts = cleanNames(bridgeAccounts, true);
        ignores = cleanNames(ignores, false);
    }

    private static List<String> cleanNames(List<String> names, boolean defaultBridgeAccounts) {
        List<String> cleaned = new ArrayList<>();
        if (names != null) {
            for (String name : names) {
                if (name == null) continue;
                String trimmed = name.trim();
                if (!trimmed.isEmpty() && cleaned.stream().noneMatch(existing -> existing.equalsIgnoreCase(trimmed))) cleaned.add(trimmed);
            }
        }
        if (cleaned.isEmpty() && defaultBridgeAccounts) cleaned.addAll(List.of("MrTheAFK", "lfForagingUpdate"));
        return cleaned;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
