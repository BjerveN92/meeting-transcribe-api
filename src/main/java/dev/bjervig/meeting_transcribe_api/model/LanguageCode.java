package dev.bjervig.meeting_transcribe_api.model;

import java.util.Locale;
import java.util.Set;

//  * Validerar och normaliserar ISO 639-1-språkkoder (t.ex. "sv", "en", "hu", "uk").

public final class LanguageCode {

    private static final Set<String> ISO_LANGUAGES = Set.of(Locale.getISOLanguages());

    private LanguageCode() {
    }

    /**
     * @return normaliserad kod i gemener, eller null om värdet är tomt
     * @throws IllegalArgumentException om koden inte är en giltig ISO 639-1-kod
     */
    public static String normalize(String value, String paramName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String code = value.trim().toLowerCase(Locale.ROOT);
        if (!ISO_LANGUAGES.contains(code)) {
            throw new IllegalArgumentException(
                    "Ogiltig språkkod för '%s': '%s'. Använd ISO 639-1, t.ex. sv, en, hu, uk."
                            .formatted(paramName, value.trim()));
        }
        return code;
    }

    /** Engelskt språknamn, t.ex. "hu" -> "Hungarian". Tydligast för LLM:en. */
    public static String displayName(String code) {
        return Locale.of(code).getDisplayLanguage(Locale.ENGLISH);
    }
}
