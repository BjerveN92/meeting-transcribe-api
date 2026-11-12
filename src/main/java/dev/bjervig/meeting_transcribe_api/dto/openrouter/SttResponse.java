package dev.bjervig.meeting_transcribe_api.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SttResponse(
    String text,
    Usage usage,
    String task,
    String language,
    @JsonProperty("language_confidence") Double languageConfidence,
    Double duration,
    Double confidence,
    List<Segment> segments,
    List<Word> words,
    List<Entity> entities
) {
    public record Usage(
        double cost,
        @JsonProperty("input_tokens") int inputTokens,
        @JsonProperty("output_tokens") int outputTokens,
        double seconds,
        @JsonProperty("total_tokens") int totalTokens
    ) {}

    public record Segment(
        int id,
        double start,
        double end,
        String text,
        String speaker,
        @JsonProperty("speaker_label") String speakerLabel,
        Integer channel,
        @JsonProperty("avg_logprob") Double avgLogprob,
        @JsonProperty("compression_ratio") Double compressionRatio,
        @JsonProperty("no_speech_prob") Double noSpeechProb
    ) {}

    public record Word(
        String word,
        double start,
        double end,
        Double confidence,
        String speaker,
        @JsonProperty("speaker_label") String speakerLabel,
        String type
    ) {}

    public record Entity(
        String text,
        String type,
        @JsonProperty("start_char") int startChar,
        @JsonProperty("end_char") int endChar
    ) {}
}
