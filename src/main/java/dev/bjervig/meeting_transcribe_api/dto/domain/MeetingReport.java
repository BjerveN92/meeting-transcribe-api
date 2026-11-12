package dev.bjervig.meeting_transcribe_api.dto.domain;

import java.time.Instant;

public record MeetingReport(
    MeetingSummary summary,
    Transcription transcription,
    TranscodeResult transcodeInfo,
    Instant createdAt
) {}
