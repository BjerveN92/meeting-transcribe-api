package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.client.OpenRouterClient;
import dev.bjervig.meeting_transcribe_api.dto.domain.TranscodeResult;
import dev.bjervig.meeting_transcribe_api.dto.domain.Transcription;
import dev.bjervig.meeting_transcribe_api.dto.openrouter.SttRequest;
import dev.bjervig.meeting_transcribe_api.dto.openrouter.SttResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranscribeService {

    private final OpenRouterClient client;

    @Value("${openrouter.models.stt}")
    private String sttModel;

    /**
     * Leverantörens slug på OpenRouter (t.ex. "mistral", "deepgram") som ska få
     * diarize=true. Tomt = ingen diarisering.
     */
    @Value("${openrouter.stt.diarize-provider:}")
    private String diarizeProvider;

    public Transcription transcribe(TranscodeResult audio, String language)
            throws IOException {
        byte[] bytes = Files.readAllBytes(audio.outputPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);

        SttRequest request = SttRequest.withBase64(
            sttModel, base64, audio.outputFormat(),
            language, // null = låt modellen autodetektera språket
            providerOptions()
        );

        SttResponse response = client.stt(request);
        return Transcription.from(response);
    }

    private Map<String, Object> providerOptions() {
        if (diarizeProvider == null || diarizeProvider.isBlank()) {
            return null;
        }
        return Map.of("options",
            Map.of(diarizeProvider.trim(), Map.of("diarize", true)));
    }
}
