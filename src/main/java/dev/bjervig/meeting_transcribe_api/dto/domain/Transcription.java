package dev.bjervig.meeting_transcribe_api.dto.domain;

import dev.bjervig.meeting_transcribe_api.dto.openrouter.SttResponse;
import java.util.List;

public record Transcription(
    String rawText,
    List<SttResponse.Segment> segments,
    double audioDurationSeconds,
    double sttCost
) {
    public static Transcription from(SttResponse response) {
        return new Transcription(
            response.text(),
            response.segments() != null ? response.segments() : List.of(),
            response.usage() != null ? response.usage().seconds() : 0,
            response.usage() != null ? response.usage().cost() : 0
        );
    }
}
