package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.model.AnalysisProfile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

@Component
public class PromptLoader {

    private final Map<AnalysisProfile, String> cache =
        new EnumMap<>(AnalysisProfile.class);

    public String load(AnalysisProfile profile) {
        return cache.computeIfAbsent(profile, p -> {
            try (var is = getClass().getClassLoader()
                    .getResourceAsStream(p.getPromptResource())) {
                if (is == null) {
                    throw new IllegalStateException(
                        "Prompt-fil saknas: " + p.getPromptResource());
                }
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
