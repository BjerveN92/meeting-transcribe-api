package dev.bjervig.meeting_transcribe_api.dto.domain;

import dev.bjervig.meeting_transcribe_api.model.AnalysisProfile;
import java.util.List;
import java.util.stream.Collectors;

public record RelevantContext(
    List<ContextChunk> chunks,
    AnalysisProfile profile
) {
    /**
     * Formaterar kontexten som avgränsad DATA för user-meddelandet.
     * Får aldrig läggas i systemprompten – innehållet kommer från
     * användaruppladdade dokument och är inte betrott.
     */
    public String toPromptSection() {
        if (chunks.isEmpty()) return "";
        return chunks.stream()
            .map(c -> "<document source=\"%s\">\n%s\n</document>".formatted(
                PromptSanitizer.attribute(c.sourceDocument()),
                PromptSanitizer.content(c.content())))
            .collect(Collectors.joining("\n", "<context>\n", "\n</context>"));
    }
}
