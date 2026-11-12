package dev.bjervig.meeting_transcribe_api.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SttRequest(
    String model,
    @JsonProperty("input_audio") InputAudio inputAudio,
    String language,
    Double temperature,
    @JsonProperty("response_format") String responseFormat,
    @JsonProperty("timestamp_granularities") List<String> timestampGranularities,
    List<String> keyterms,
    /**
     * Leverantörsspecifika inställningar, t.ex. diarisering:
     * {"options": {"mistral": {"diarize": true}}}.
     * OpenRouter skickar bara vidare options för den leverantör som faktiskt kör anropet.
     */
    Map<String, Object> provider
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InputAudio(
        String data,
        String format,
        String url
    ) {}

    public static SttRequest withBase64(String model, String base64,
                                         String format, String language,
                                         Map<String, Object> provider) {
        return new SttRequest(
            model,
            new InputAudio(base64, format, null),
            language, null, "verbose_json",
            List.of("segment"), null, provider
        );
    }
}
