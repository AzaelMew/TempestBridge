# TempestBridge

TempestBridge is a client-side Fabric mod for Minecraft 26.1.2 that formats Tempest guild bridge messages locally in chat.

It is a Fabric port of the original ChatTriggers module. External integrations from the ChatTriggers version, such as ItemSharing, are intentionally not recreated here; this mod focuses on the main client-side chat formatting behavior.

## Features

- Formats Tempest guild bridge messages in guild chat.
- Formats officer bridge messages as staff messages.
- Formats SkyBlock command output for:
  - networth
  - stats
  - catacombs
  - slayers
  - skills
  - kuudra
- Formats bridge event/statcheck messages.
- Optional event ping sound.
- Local ignore list for bridged users:
  - `/bridgeignore <ign>`
  - `/bridgeunignore <ign>`
- STuF-style URL encode/decode support for bridge-safe links.
- Settings screen through Mod Menu + Cloth Config.
- Configurable bridge account usernames, so the formatter can be pointed at other bridge bots instead of only the default Tempest bot accounts.
- Configurable display formatting for bridge chat:
  - message author color
  - bridge tag text, for example `[DISCORD]`
  - bridge tag color
  - guild-rank tag color
  - SkyBlock event/stat colors
  - SkyBlock command output symbol/text/value colors
  - optional ping-name highlight with ping sound
- Debug logging toggle for troubleshooting live chat matching:
  - `/bridgedebug`

## Commands

```text
/bridge
```

Opens the TempestBridge settings screen.

```text
/bridgedebug
```

Toggles debug logging. When enabled, matching details are written to `logs/latest.log`.

```text
/bridgeignore <ign>
/bridgeunignore <ign>
```

Adds or removes a username from the local bridge ignore list.

## Requirements

- Minecraft `26.1.2`
- Java `25+`
- Fabric Loader `0.19.3+`
- Fabric API
- Mod Menu
- Cloth Config

## Building

```bash
gradle build
```

The built mod jar is produced at:

```text
build/libs/tempestbridge-1.0.2.jar
```

## Notes on Minecraft 26.1.2 mappings

Fabric has discontinued official Yarn mappings for Minecraft 26.1 and above. Minecraft 26.1.2 is distributed unobfuscated, so this project is written against Mojang/official names and uses an identity Tiny mapping jar:

```text
gradle/mappings/identity-mappings-26.1.2.jar
```

This means the mapping names are effectively:

```text
official == intermediary == named
```

## Configuration

Open the settings screen with `/bridge`, or through Mod Menu.

The most important setting for using this mod with other bridge bots is:

```text
Bridge Account Usernames
```

Set this to a comma-separated list of Minecraft usernames that should be treated as bridge accounts, for example:

```text
MrTheAFK, lfForagingUpdate, SomeOtherBridgeBot
```

When a chat line is sent by one of these accounts, TempestBridge will apply the same bridge formatting/parsing rules to it. This lets the mod work with renamed/replaced bridge bot accounts without rebuilding the mod.

The settings screen also lets you change the visible bridge formatting, including the displayed bridge tag text (`DISCORD` by default), tag colors, author colors, guild-rank tag colors, event colors, command output colors, and the optional ping-name highlight. `Ping Name` accepts a comma-separated list, highlights matching names in bridge chat, and plays the same ping sound used by event notifications.

The config is saved as:

```text
.minecraft/config/tempestbridge.json
```

In a Gradle dev run, it is saved under:

```text
run/config/tempestbridge.json
```

## Scope

This is a client-side display/formatting mod. It does not implement or host a Discord bridge, backend bridge service, ItemSharing, remote API integrations, or server-side behavior.
