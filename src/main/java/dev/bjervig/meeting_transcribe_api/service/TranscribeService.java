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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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

    /**
     * Transkriberar alla ljudbitar i tur och ordning och slår ihop dem till en
     * transkribering. Tidsstämplar i senare bitar förskjuts med bitens startposition.
     */
    public Transcription transcribe(TranscodeResult audio, String language)
            throws IOException {
        var text = new StringBuilder();
        List<SttResponse.Segment> segments = new ArrayList<>();
        double duration = 0;
        double cost = 0;

        List<Path> chunks = audio.chunks();
        for (int i = 0; i < chunks.size(); i++) {
            Transcription part = transcribeOne(chunks.get(i), audio.outputFormat(), language);
            double offset = (double) i * audio.chunkSeconds();

            if (part.rawText() != null && !part.rawText().isBlank()) {
                if (!text.isEmpty()) {
                    text.append('\n');
                }
                text.append(part.rawText().strip());
            }
            for (SttResponse.Segment s : part.segments()) {
                segments.add(new SttResponse.Segment(
                        segments.size(), s.start() + offset, s.end() + offset,
                        s.text(), s.speaker(), s.speakerLabel(), s.channel(),
                        s.avgLogprob(), s.compressionRatio(), s.noSpeechProb()));
            }
            duration += part.audioDurationSeconds();
            cost += part.sttCost();
        }

        return new Transcription(text.toString(), segments, duration, cost);
    }

    private Transcription transcribeOne(Path file, String format, String language)
            throws IOException {
        String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(file));

        SttRequest request = SttRequest.withBase64(
            sttModel, base64, format,
            language, // null = låt modellen autodetektera språket
            providerOptions()
        );

        return Transcription.from(client.stt(request));
    }

    private Map<String, Object> providerOptions() {
        if (diarizeProvider == null || diarizeProvider.isBlank()) {
            return null;
        }
        return Map.of("options",
            Map.of(diarizeProvider.trim(), Map.of("diarize", true)));
    }
}
