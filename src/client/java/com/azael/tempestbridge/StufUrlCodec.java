package com.azael.tempestbridge;

import java.util.ArrayList;
import java.util.List;

public final class StufUrlCodec {
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private StufUrlCodec() {}

    public static String encode(String url) {
        StringBuilder encoded = new StringBuilder("l$");
        if (url.startsWith("http://")) {
            encoded.append('h');
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            encoded.append('H');
            url = url.substring(8);
        } else {
            encoded.append('0');
        }

        if (url.endsWith(".png")) {
            encoded.append('1');
            url = url.substring(0, url.length() - 4);
        } else if (url.endsWith(".jpg")) {
            encoded.append('2');
            url = url.substring(0, url.length() - 4);
        } else if (url.endsWith(".jpeg")) {
            encoded.append('3');
            url = url.substring(0, url.length() - 5);
        } else if (url.endsWith(".gif")) {
            encoded.append('4');
            url = url.substring(0, url.length() - 4);
        } else {
            encoded.append('0');
        }

        List<Integer> dotIndices = new ArrayList<>();
        for (int i = 0; i < url.length() && i <= 8; i++) {
            if (url.charAt(i) == '.') dotIndices.add(i);
        }

        String first9 = url.substring(0, Math.min(9, url.length())).replace(".", "");
        String then = url.length() > 9 ? url.substring(9).replace('.', '^') : "";
        String shifted = charInc(first9 + then, 1);
        for (int i : dotIndices) encoded.append(i);
        encoded.append('|').append(shifted);
        return encoded.toString();
    }

    public static String decode(String string) {
        if (!string.startsWith("l$") || string.length() < 5 || !string.contains("|")) {
            throw new IllegalArgumentException("String does not appear to be STuF encoded");
        }
        char prefix = string.charAt(2);
        char suffix = string.charAt(3);
        int pipe = string.indexOf('|');
        String dotPart = string.substring(4, pipe);
        String urlBody = string.substring(pipe + 1);

        int firstLen = Math.max(0, 9 - dotPart.length());
        String first9 = urlBody.substring(0, Math.min(firstLen, urlBody.length()));
        String then = urlBody.length() > firstLen ? urlBody.substring(firstLen).replace('^', '.') : "";
        String url = charInc(first9 + then, -1);

        for (int i = 0; i < dotPart.length(); i++) {
            int index = Character.digit(dotPart.charAt(i), 10);
            if (index >= 0 && index <= url.length()) {
                url = url.substring(0, index) + "." + url.substring(index);
            }
        }

        if (prefix == 'h') url = "http://" + url;
        else if (prefix == 'H') url = "https://" + url;

        if (suffix == '1') url += ".png";
        else if (suffix == '2') url += ".jpg";
        else if (suffix == '3') url += ".jpeg";
        else if (suffix == '4') url += ".gif";
        return url;
    }

    public static String decodeWords(String message) {
        String[] words = message.split(" ", -1);
        boolean changed = false;
        for (int i = 0; i < words.length; i++) {
            String stripped = stripFormatting(words[i]);
            if (stripped.startsWith("l$")) {
                try {
                    words[i] = decode(stripped);
                    changed = true;
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return changed ? String.join(" ", words) : message;
    }

    public static String encodeUrlsInMessage(String message) {
        String[] words = message.split(" ", -1);
        boolean changed = false;
        for (int i = 0; i < words.length; i++) {
            if (words[i].startsWith("http")) {
                words[i] = encode(words[i]);
                changed = true;
            }
        }
        return changed ? String.join(" ", words) : message;
    }

    private static String charInc(String str, int amount) {
        StringBuilder out = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int index = CHAR_SET.indexOf(c);
            if (index == -1) {
                out.append(c);
            } else {
                int offset = (index + amount) % CHAR_SET.length();
                if (offset < 0) offset += CHAR_SET.length();
                out.append(CHAR_SET.charAt(offset));
            }
        }
        return out.toString();
    }

    private static String stripFormatting(String input) {
        return input.replaceAll("(?i)§[0-9A-FK-OR]", "");
    }
}
