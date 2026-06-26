package com.azael.tempestbridge;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;



public final class TempestBridgeConfigScreen {
    private TempestBridgeConfigScreen() {}

    public static Screen create(Screen parent) {
        TempestBridgeConfig config = TempestBridgeClient.CONFIG;
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("TempestBridge"));
        builder.setSavingRunnable(config::save);
        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        ConfigCategory commands = builder.getOrCreateCategory(Component.literal("Commands"));
        ConfigCategory events = builder.getOrCreateCategory(Component.literal("Skyblock Events"));

        String[] colors = TempestBridgeClient.COLOR_NAMES;
        String[] symbols = TempestBridgeClient.SYMBOLS;

        general.addEntry(entry.startSelector(Component.literal("Message Author Color"), colors, colors[config.messageAuthorColor])
            .setDefaultValue(colors[11])
            .setTooltip(Component.literal("Guild > Azael_Nya [DISCORD]: author name color"))
            .setSaveConsumer(value -> config.messageAuthorColor = indexOf(colors, value))
            .build());
        general.addEntry(entry.startSelector(Component.literal("Discord Tag Color"), colors, colors[config.discordTagColor])
            .setDefaultValue(colors[14])
            .setTooltip(Component.literal("Color of the fallback [DISCORD] tag"))
            .setSaveConsumer(value -> config.discordTagColor = indexOf(colors, value))
            .build());
        general.addEntry(entry.startStrField(Component.literal("Ping Name"), config.pingName)
            .setDefaultValue("")
            .setTooltip(Component.literal("Kept for parity with the ChatTriggers settings"))
            .setSaveConsumer(value -> config.pingName = value)
            .build());
        general.addEntry(entry.startStrField(Component.literal("Discord Tag"), config.discordTagText)
            .setDefaultValue("DISCORD")
            .setSaveConsumer(value -> config.discordTagText = value)
            .build());
        general.addEntry(entry.startSelector(Component.literal("Guild Rank Tag Color"), colors, colors[config.rankTagColor])
            .setDefaultValue(colors[6])
            .setSaveConsumer(value -> config.rankTagColor = indexOf(colors, value))
            .build());
        general.addEntry(entry.startBooleanToggle(Component.literal("Show Discord Messages"), config.discordToggle)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.discordToggle = value)
            .build());
        general.addEntry(entry.startBooleanToggle(Component.literal("Debug Logging"), config.debugLogging)
            .setDefaultValue(false)
            .setTooltip(Component.literal("Log bridge detection details to logs/latest.log"))
            .setSaveConsumer(value -> config.debugLogging = value)
            .build());

        events.addEntry(entry.startBooleanToggle(Component.literal("Show Skyblock Events"), config.shouldShowEvent)
            .setDefaultValue(true)
            .setSaveConsumer(value -> config.shouldShowEvent = value)
            .build());
        events.addEntry(entry.startBooleanToggle(Component.literal("Should Skyblock Event Ping"), config.shouldEventPing)
            .setDefaultValue(false)
            .setTooltip(Component.literal("Play an orb sound when bridge event/statcheck messages arrive"))
            .setSaveConsumer(value -> config.shouldEventPing = value)
            .build());
        events.addEntry(entry.startSelector(Component.literal("Skyblock Event Tag Color"), colors, colors[config.eventTagColor])
            .setDefaultValue(colors[6])
            .setSaveConsumer(value -> config.eventTagColor = indexOf(colors, value))
            .build());
        events.addEntry(entry.startSelector(Component.literal("Skyblock Event Text Color"), colors, colors[config.eventTextColor])
            .setDefaultValue(colors[11])
            .setSaveConsumer(value -> config.eventTextColor = indexOf(colors, value))
            .build());
        events.addEntry(entry.startSelector(Component.literal("Skyblock Event Time Color"), colors, colors[config.eventTimeColor])
            .setDefaultValue(colors[15])
            .setSaveConsumer(value -> config.eventTimeColor = indexOf(colors, value))
            .build());

        commands.addEntry(entry.startSelector(Component.literal("Command Symbol"), symbols, symbols[config.commandSymbol])
            .setDefaultValue(symbols[0])
            .setSaveConsumer(value -> config.commandSymbol = indexOf(symbols, value))
            .build());
        commands.addEntry(entry.startSelector(Component.literal("Stat Text Color"), colors, colors[config.commandTextColor])
            .setDefaultValue(colors[11])
            .setSaveConsumer(value -> config.commandTextColor = indexOf(colors, value))
            .build());
        commands.addEntry(entry.startSelector(Component.literal("Stat Value Color"), colors, colors[config.commandValueColor])
            .setDefaultValue(colors[14])
            .setSaveConsumer(value -> config.commandValueColor = indexOf(colors, value))
            .build());

        return builder.build();
    }

    private static int indexOf(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) return i;
        }
        return 0;
    }
}
