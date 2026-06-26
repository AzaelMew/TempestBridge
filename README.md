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
- Debug logging toggle for troubleshooting live chat matching:
  - `/tempestdebug`

## Commands

```text
/tempest
```

Opens the TempestBridge settings screen.

```text
/tempestdebug
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
build/libs/tempestbridge-1.0.0.jar
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
