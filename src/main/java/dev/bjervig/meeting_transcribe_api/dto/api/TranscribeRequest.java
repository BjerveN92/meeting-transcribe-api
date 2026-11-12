package dev.bjervig.meeting_transcribe_api.dto.api;

public record TranscribeRequest(
    String profile,
    String language
) {}
