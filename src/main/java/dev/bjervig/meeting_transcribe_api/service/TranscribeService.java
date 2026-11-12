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

@Service
@RequiredArgsConstructor
public class TranscribeService {

    private final OpenRouterClient client;

    @Value("${openrouter.models.stt}")
    private String sttModel;

    public Transcription transcribe(TranscodeResult audio, String language)
            throws IOException {
        byte[] bytes = Files.readAllBytes(audio.outputPath());
        String base64 = Base64.getEncoder().encodeToString(bytes);

        SttRequest request = SttRequest.withBase64(
            sttModel, base64, audio.outputFormat(),
            language != null ? language : "sv"
        );

        SttResponse response = client.stt(request);
        return Transcription.from(response);
    }
}
