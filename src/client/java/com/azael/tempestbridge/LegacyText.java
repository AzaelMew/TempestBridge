package com.azael.tempestbridge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class LegacyText {
    private LegacyText() {}

    public static Component toComponent(String input) {
        MutableComponent root = Component.empty();
        Style style = Style.EMPTY;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '§' || c == '&') && i + 1 < input.length()) {
                ChatFormatting formatting = fromCode(input.charAt(++i));
                if (formatting != null) {
                    if (!current.isEmpty()) {
                        root.append(Component.literal(current.toString()).setStyle(style));
                        current.setLength(0);
                    }
                    if (formatting == ChatFormatting.RESET) {
                        style = Style.EMPTY;
                    } else {
                        style = style.applyFormat(formatting);
                    }
                    continue;
                }
                current.append(c).append(input.charAt(i));
            } else {
                current.append(c);
            }
        }
        if (!current.isEmpty()) root.append(Component.literal(current.toString()).setStyle(style));
        return root;
    }

    private static ChatFormatting fromCode(char raw) {
        char code = Character.toLowerCase(raw);
        return ChatFormatting.getByCode(code);
    }
}
