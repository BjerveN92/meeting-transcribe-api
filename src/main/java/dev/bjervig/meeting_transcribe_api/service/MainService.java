package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.dto.domain.MeetingReport;
import dev.bjervig.meeting_transcribe_api.dto.domain.MeetingSummary;
import dev.bjervig.meeting_transcribe_api.dto.domain.RelevantContext;
import dev.bjervig.meeting_transcribe_api.dto.domain.TranscodeResult;
import dev.bjervig.meeting_transcribe_api.dto.domain.Transcription;
import dev.bjervig.meeting_transcribe_api.model.AnalysisProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MainService {

    private final TranscodingService transcodingService;
    private final TranscribeService transcribeService;
    private final ContextService contextService;
    private final SummaryService summaryService;

    public MeetingReport process(Path audioFile,
            AnalysisProfile profile,
            String language,
            String outputLanguage) throws IOException {

        // 1. Komprimera vid behov
        TranscodeResult transcoded = transcodingService.process(audioFile);

        // 2. Transkribera via OpenRouter STT
        Transcription transcription = transcribeService.transcribe(
                transcoded, language);

        // 3. Hämta relevant RAG-kontext
        RelevantContext context = contextService.retrieve(
                transcription, profile);

        // 4. Analysera via OpenRouter LLM
        MeetingSummary summary = summaryService.summarize(
                transcription, profile, context, outputLanguage);

        // 5. Paketera
        return new MeetingReport(
                summary, transcription, transcoded, Instant.now());
    }
}
