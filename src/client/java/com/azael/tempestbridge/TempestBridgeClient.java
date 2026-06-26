package com.azael.tempestbridge;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TempestBridgeClient implements ClientModInitializer {
    public static final String MOD_ID = "tempestbridge";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static TempestBridgeConfig CONFIG;

    static final String[] COLORS = {"§0", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§a", "§b", "§c", "§d", "§e", "§f"};
    static final String[] COLOR_NAMES = {"§0Black", "§1Dark Blue", "§2Dark Green", "§3Dark Aqua", "§4Dark Red", "§5Dark Purple", "§6Gold", "§7Gray", "§8Dark Gray", "§9Blue", "§aGreen", "§bAqua", "§cRed", "§dPink", "§eYellow", "§fWhite"};
    static final String[] SYMBOLS = {"-", "*", "‣", "►", "➣", "➢", "❥", "✯", "➤", "➺"};

    private static final Pattern BRIDGE_ACCOUNT_PREFIX = Pattern.compile("^(?:Guild|Officer) > ((?:\\[[^\\]]+] )?([A-Za-z0-9_]{1,16}) \\[[^\\]]+]):");
    private static final Pattern GUILD_JOIN_LEAVE = Pattern.compile("(§2Guild >§[A-Za-z0-9_] )([A-Za-z0-9_]{2,16} )(left\\.|joined\\.)");
    private static final Pattern STAT_PAIR = Pattern.compile("([\\w ]+): ([\\d\\-,$]+)");
    private static final Pattern CATA_PAIR = Pattern.compile("([\\w ]+): ([\\d\\-,.$]+)");
    private static final Pattern SKILL_PAIR = Pattern.compile("([\\w ]+): ([\\d-]+)");
    private static final Pattern SLAYER_TOTAL = Pattern.compile("(Total Slayer EXP):  ?([\\d,.]+)");
    private static final Pattern SLAYER_LEVEL = Pattern.compile("( [\\w]+) level:  ?(\\d) ? ?- ?([\\d,.]+)(?:xp)?");

    @Override
    public void onInitializeClient() {
        CONFIG = TempestBridgeConfig.load();
        registerCommands();

        ClientSendMessageEvents.MODIFY_CHAT.register(StufUrlCodec::encodeUrlsInMessage);

        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
            return allowIncoming("ALLOW_CHAT", message);
        });

        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (overlay) return true;
            return allowIncoming("ALLOW_GAME", message);
        });
    }

    private static boolean allowIncoming(String source, Component component) {
        String raw = component.getString();
        String strippedCurl = stripCurl(raw);
        String msg = stripLegacyFormatting(strippedCurl).trim();
        debug("{} raw='{}' strippedCurl='{}' plain='{}'", source, raw, strippedCurl, msg);
        String account = findAccount(msg);
        if (account == null) {
            debug("{} no bridge account matched", source);
            return true;
        }
        debug("{} matched account='{}'", source, account);
        if (isIgnored(msg, account)) {
            debug("{} ignored matched message for account='{}'", source, account);
            return false;
        }
        String transformed = transform(msg, account);
        if (transformed == null) {
            debug("{} account matched but no transform branch matched", source);
            return true;
        }
        debug("{} transformed='{}'", source, transformed);
        if (!transformed.isEmpty()) chat(transformed);
        return false;
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("tempest")
                .executes(ctx -> {
                    Minecraft client = Minecraft.getInstance();
                    client.execute(() -> client.setScreen(TempestBridgeConfigScreen.create(client.screen)));
                    return 1;
                })
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("bridgeignore")
                .then(ClientCommandManager.argument("ign", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        String ign = StringArgumentType.getString(ctx, "ign").trim();
                        if (!ign.isEmpty() && CONFIG.ignores.stream().noneMatch(s -> s != null && s.equalsIgnoreCase(ign))) {
                            CONFIG.ignores.add(ign);
                            CONFIG.save();
                        }
                        chat("§2Guild > §aIgnored " + color(CONFIG.messageAuthorColor) + ign);
                        return 1;
                    }))
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("bridgeunignore")
                .then(ClientCommandManager.argument("ign", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        String ign = StringArgumentType.getString(ctx, "ign").trim();
                        CONFIG.ignores.removeIf(s -> s != null && s.equalsIgnoreCase(ign));
                        CONFIG.save();
                        chat("§2Guild > §cUn-Ignored " + color(CONFIG.messageAuthorColor) + ign);
                        return 1;
                    }))
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandManager.literal("tempestdebug")
                .executes(ctx -> {
                    CONFIG.debugLogging = !CONFIG.debugLogging;
                    CONFIG.save();
                    chat("§2Guild > §aTempestBridge debug logging " + (CONFIG.debugLogging ? "enabled" : "disabled") + "§a. Check logs/latest.log");
                    LOGGER.info("TempestBridge debug logging {} by /tempestdebug", CONFIG.debugLogging ? "enabled" : "disabled");
                    return 1;
                })
        ));
    }

    private static void debug(String message, Object... args) {
        if (CONFIG != null && CONFIG.debugLogging) LOGGER.info("[TempestBridge debug] " + message, args);
    }

    private static String transform(String msg, String account) {
        if (msg.contains("'s networth") && msg.contains("˚")) return handleNetworth(msg);
        if ((msg.contains("'s stats:") || msg.contains("'s stats: On")) && msg.contains("˚")) return handleStats(msg);
        if (msg.contains("'s cata") && msg.contains("˚")) return handleCata(msg);
        if (msg.contains("'s slayers") && msg.contains("˚")) return handleSlayers(msg);
        if (msg.contains("'s skills") && msg.contains("˚")) return handleSkills(msg);
        if (msg.contains("'s kuudra") && msg.contains("˚")) return handleKuudra(msg);
        if (msg.contains("[EVENT]")) return handleEvent(msg, account);
        if (msg.contains("[STATCHECK]")) return handleStatcheck(msg, account);
        if (msg.contains("The next contest starts in:")) return handleContest(msg, account);
        if (msg.startsWith("Guild > " + account)) return handleMessage(msg, account);
        if (msg.startsWith("Officer > " + account)) return handleOfficer(msg, account);
        return null;
    }

    private static String handleNetworth(String msg) {
        String body = stripBridgePrefix(msg);
        String[] parts = body.split("˚");
        String first = parts.length > 0 ? parts[0].trim() : body.trim();

        Matcher title = Pattern.compile("^(.+?'s networth):.*$").matcher(first);
        String header = title.matches() ? title.group(1) + ":" : first.replaceFirst(":.*$", ":");
        StringBuilder out = new StringBuilder(color(CONFIG.commandTextColor)).append(header);

        for (int i = 1; i < parts.length; i++) {
            String line = parts[i].trim();
            if (line.isEmpty()) continue;
            Matcher pair = Pattern.compile("^([^:]+):\\s*(.*)$").matcher(line);
            if (pair.matches()) {
                out.append("\n ")
                    .append(color(CONFIG.commandTextColor)).append(symbol()).append(" ")
                    .append(pair.group(1).trim()).append("&f: ")
                    .append(color(CONFIG.commandValueColor)).append(pair.group(2).trim());
            } else {
                out.append("\n ").append(color(CONFIG.commandTextColor)).append(line);
            }
        }
        return out.toString();
    }

    private static String handleStats(String msg) {
        String body = stripBridgePrefix(msg);
        Matcher matcher = STAT_PAIR.matcher(body.replace("˚", "\n"));
        String out = replacePairs(matcher, false);
        return color(CONFIG.commandTextColor) + out.replace("☠ ", "");
    }

    private static String handleCata(String msg) {
        String body = stripBridgePrefix(msg);
        Matcher matcher = CATA_PAIR.matcher(body.replace("˚", "\n"));
        return color(CONFIG.commandTextColor) + replacePairs(matcher, false);
    }

    private static String handleSlayers(String msg) {
        String body = stripBridgePrefix(msg);
        String replaced = SLAYER_TOTAL.matcher(body.replace("˚", "\n")).replaceAll(" " + color(CONFIG.commandTextColor) + symbol() + " $1&f: " + color(CONFIG.commandValueColor) + "$2");
        replaced = SLAYER_LEVEL.matcher(replaced).replaceAll(" " + color(CONFIG.commandTextColor) + symbol() + " $1 " + color(CONFIG.commandValueColor) + "$2&f: " + color(CONFIG.commandValueColor) + "$3");
        return color(CONFIG.commandTextColor) + replaced;
    }

    private static String handleSkills(String msg) {
        String body = stripBridgePrefix(msg);
        Matcher matcher = SKILL_PAIR.matcher(body.replace("˚", "\n"));
        return color(CONFIG.commandTextColor) + replacePairs(matcher, true);
    }

    private static String handleKuudra(String msg) {
        String body = stripBridgePrefix(msg);
        return color(CONFIG.commandTextColor) + body.replace("˚", "\n")
            .replace("Current Faction:", "\n " + color(CONFIG.commandTextColor) + symbol() + " Current Faction&f:" + color(CONFIG.commandValueColor))
            .replaceAll("([\\w ]+): ([\\d-]+)", " " + color(CONFIG.commandTextColor) + symbol() + "$1&f: " + color(CONFIG.commandValueColor) + "$2");
    }

    private static String handleContest(String msg, String account) {
        return msg.replace("Guild > " + account + ": ", color(CONFIG.commandTextColor));
    }

    private static String handleEvent(String msg, String account) {
        if (!CONFIG.shouldShowEvent) return "";
        if (CONFIG.shouldEventPing) ding();
        return msg.replace("Guild > " + account + ": ", color(CONFIG.commandTextColor))
            .replace("[EVENT]", color(CONFIG.eventTagColor) + "[EVENT]" + color(CONFIG.eventTextColor))
            .replace(":", ":" + color(CONFIG.eventTimeColor));
    }

    private static String handleStatcheck(String msg, String account) {
        if (CONFIG.shouldEventPing) ding();
        return msg.replace("Officer > " + account + ": ", color(CONFIG.commandTextColor))
            .replace("[STATCHECK]", color(CONFIG.eventTagColor) + "[STATCHECK]" + color(CONFIG.eventTextColor))
            .replace(". SB", ".\n" + color(CONFIG.eventTextColor) + "SB")
            .replace(":", ":" + color(CONFIG.eventTimeColor));
    }

    private static String handleMessage(String msg, String account) {
        if (!CONFIG.discordToggle) return "";
        msg = msg.replace("Guild > " + account + ":", "§2Guild >" + color(CONFIG.messageAuthorColor));
        Matcher joinLeave = GUILD_JOIN_LEAVE.matcher(msg);
        if (joinLeave.find()) msg = joinLeave.replaceAll("$1$2§e$3");
        msg = applyTag(msg);
        return applyPingHighlight(StufUrlCodec.decodeWords(msg));
    }

    private static String handleOfficer(String msg, String account) {
        if (!CONFIG.discordToggle) return "";
        msg = msg.replace("Officer > " + account + ":", "§3Staff >" + color(CONFIG.messageAuthorColor));
        msg = applyTag(msg);
        return applyPingHighlight(StufUrlCodec.decodeWords(msg));
    }

    private static String applyPingHighlight(String msg) {
        if (CONFIG.pingName == null || CONFIG.pingName.trim().isEmpty()) return msg;

        String highlighted = msg;
        boolean matched = false;
        for (String rawName : CONFIG.pingName.split(",")) {
            String name = rawName.trim();
            if (name.isEmpty()) continue;

            String prefix = name.startsWith("@") ? "" : "@?";
            Pattern pattern = Pattern.compile("(?i)(?<![A-Za-z0-9_])(" + prefix + Pattern.quote(name) + ")(?![A-Za-z0-9_])");
            Matcher matcher = pattern.matcher(highlighted);
            StringBuffer out = new StringBuffer();
            while (matcher.find()) {
                matched = true;
                matcher.appendReplacement(out, Matcher.quoteReplacement("§6§n" + matcher.group(1) + "§r"));
            }
            matcher.appendTail(out);
            highlighted = out.toString();
        }
        if (matched) ding();
        return highlighted;
    }

    private static String applyTag(String msg) {
        String textInput = "[" + CONFIG.discordTagText + "]";
        if (msg.contains(": ") && msg.contains("]: ")) {
            int start = msg.indexOf('[');
            int end = msg.indexOf(']', start);
            if (start >= 0 && end > start) textInput = msg.substring(start, end + 1);
            msg = msg.replaceAll("\\[[a-zA-Z]+\\]", "");
            msg = msg.replaceFirst(": ", color(CONFIG.rankTagColor) + Matcher.quoteReplacement(textInput) + "§f: ");
        } else {
            msg = msg.replaceFirst(": ", " " + color(CONFIG.discordTagColor) + Matcher.quoteReplacement(textInput) + "§f: ");
        }
        return msg;
    }

    private static String replacePairs(Matcher matcher, boolean requireKnownReplacement) {
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String value = matcher.group(2);
            String name = replacementFor(key);
            if (name == null) {
                if (requireKnownReplacement) name = key;
                else name = key;
            }
            matcher.appendReplacement(out, Matcher.quoteReplacement(" " + color(CONFIG.commandTextColor) + symbol() + " " + name + "&f: " + color(CONFIG.commandValueColor) + value));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String replacementFor(String key) {
        return switch (key) {
            case "Skill Avg" -> "Skill Average";
            case "Farm" -> "Farming Level";
            case "Mine" -> "Mining Level";
            case "Comb" -> "Combat Level";
            case "Forage" -> "Foraging Level";
            case "Fish" -> "Fishing Level";
            case "Ench" -> "Enchanting Level";
            case "Alch" -> "Alchemy Level";
            case "Carp" -> "Carpentry Level";
            case "Rune" -> "Runecrafting Level";
            case "Soci" -> "Social Level";
            case "Taming" -> "Taming Level";
            case "Slayer" -> "Slayer XP";
            case "Cata" -> "☠ Cata Level";
            case "Average" -> "Φ Class Average";
            case "Archer" -> "☣ Archer Level";
            case "Berserk" -> "⚔ Berserk Level";
            case "Healer" -> "❤ Healer Level";
            case "Mage" -> "✎ Mage Level";
            case "Tank" -> "❈ Tank Level";
            default -> null;
        };
    }

    private static boolean isIgnored(String msg, String account) {
        String lowerMsg = msg.toLowerCase(Locale.ROOT);
        String lowerAccount = account.toLowerCase(Locale.ROOT);
        for (String ign : CONFIG.ignores) {
            if (ign == null) continue;
            String lowerIgn = ign.toLowerCase(Locale.ROOT);
            if (lowerMsg.contains("guild > " + lowerAccount + ": " + lowerIgn + ":")) return true;
            if (Pattern.compile("^guild > " + Pattern.quote(lowerAccount) + ": " + Pattern.quote(lowerIgn) + " \\[a-z]{1,16}\\]:", Pattern.MULTILINE).matcher(lowerMsg).find()) return true;
        }
        return false;
    }

    private static String findAccount(String msg) {
        Matcher matcher = BRIDGE_ACCOUNT_PREFIX.matcher(msg);
        if (!matcher.find()) return null;

        String account = matcher.group(1);
        String username = matcher.group(2);
        if (CONFIG.bridgeAccounts == null) return null;
        for (String configured : CONFIG.bridgeAccounts) {
            if (configured == null) continue;
            if (username.equalsIgnoreCase(normalizeConfiguredUsername(configured))) return account;
        }
        return null;
    }

    private static String normalizeConfiguredUsername(String configured) {
        String value = configured.trim();
        Matcher formatted = Pattern.compile("^(?:\\[[^\\]]+] )?([A-Za-z0-9_]{1,16})(?: \\[[^\\]]+])?$").matcher(value);
        if (formatted.matches()) return formatted.group(1);
        return value;
    }

    private static String stripCurl(String input) {
        return input.replaceAll("\\{[^}]*}$", "");
    }

    private static String stripBridgePrefix(String input) {
        return input.replaceFirst("^(?:Guild|Officer) > .+?:\\s*", "");
    }

    private static String stripLegacyFormatting(String input) {
        return input.replaceAll("(?i)§[0-9A-FK-OR]", "");
    }

    private static String color(int index) {
        return COLORS[Math.max(0, Math.min(COLORS.length - 1, index))];
    }

    private static String symbol() {
        return SYMBOLS[Math.max(0, Math.min(SYMBOLS.length - 1, CONFIG.commandSymbol))];
    }

    private static void chat(String text) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            if (client.gui != null) client.gui.getChat().addMessage(LegacyText.toComponent(text));
        });
    }

    private static void ding() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.99F, 1.0F)));
    }
}
