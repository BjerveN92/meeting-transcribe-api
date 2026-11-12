package dev.bjervig.meeting_transcribe_api.dto.domain;

import java.util.regex.Pattern;

/**
 * Neutraliserar våra egna avgränsartaggar i otrott innehåll så att
 * text i en transkribering eller ett dokument inte kan "stänga" sin
 * sektion och låtsas vara instruktioner utanför den.
 */
public final class PromptSanitizer {

    private static final Pattern DELIMITER_TAGS = Pattern.compile(
        "</?\\s*(context|document|transcript)\\b[^>]*>",
        Pattern.CASE_INSENSITIVE);

    private PromptSanitizer() {}

    public static String content(String text) {
        if (text == null) return "";
        return DELIMITER_TAGS.matcher(text).replaceAll("[borttagen tagg]");
    }

    public static String attribute(String text) {
        if (text == null) return "";
        return text.replaceAll("[\"<>\\r\\n]", "_");
    }
}
