package me.aiglez.lonkskit.players.messages;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


public class Replaceable {

    public static String handle(String string, Object... replacements) {
        if(replacements.length == 0) return string;  // no replacements
        final String[] placeholders = StringUtils.substringsBetween(string, "{", "}");
        if(placeholders.length == 0) return string; // no placeholders

        String handled = string;
        for (String possibleIndex : placeholders) { // possible match: we need to check if the string inside is an integer
            int index = NumberUtils.toInt(possibleIndex, -1);
            if(index == -1) continue;

            try {
                Object replacement = replacements[index];
                handled = StringUtils.replace(handled, "{" + index + "}", String.valueOf(replacement));
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
        return handled;
    }

    private Replaceable() {
        throw new UnsupportedOperationException();
    }
}
