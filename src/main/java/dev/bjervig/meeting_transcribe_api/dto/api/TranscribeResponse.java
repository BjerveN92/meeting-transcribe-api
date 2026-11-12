package dev.bjervig.meeting_transcribe_api.dto.api;

import dev.bjervig.meeting_transcribe_api.dto.domain.MeetingReport;
import java.time.Instant;
import java.util.List;

public record TranscribeResponse(
    String correctedText,
    List<String> bulletPoints,
    List<String> actionItems,
    List<String> decisions,
    List<String> participants,
    Meta meta
) {
    public record Meta(
        double audioDurationSeconds,
        double sttCost,
        boolean wasTranscoded,
        long originalSizeBytes,
        long compressedSizeBytes,
        Instant createdAt
    ) {}

    public static TranscribeResponse from(MeetingReport report) {
        var s = report.summary();
        var t = report.transcription();
        var tc = report.transcodeInfo();
        return new TranscribeResponse(
            s.correctedText(),
            s.bulletPoints(),
            s.actionItems(),
            s.decisions(),
            s.participants(),
            new Meta(
                t.audioDurationSeconds(),
                t.sttCost(),
                tc.wasTranscoded(),
                tc.originalSizeBytes(),
                tc.compressedSizeBytes(),
                report.createdAt()
            )
        );
    }
}
