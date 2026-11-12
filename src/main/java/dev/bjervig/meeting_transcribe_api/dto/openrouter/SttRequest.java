package dev.bjervig.meeting_transcribe_api.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SttRequest(
    String model,
    @JsonProperty("input_audio") InputAudio inputAudio,
    String language,
    Double temperature,
    @JsonProperty("response_format") String responseFormat,
    @JsonProperty("timestamp_granularities") List<String> timestampGranularities,
    Boolean diarize,
    List<String> keyterms
) {
    public record InputAudio(
        String data,
        String format,
        String url
    ) {}

    public static SttRequest withBase64(String model, String base64,
                                         String format, String language) {
        return new SttRequest(
            model,
            new InputAudio(base64, format, null),
            language, null, "verbose_json",
            List.of("segment"), true, null
        );
    }
}
