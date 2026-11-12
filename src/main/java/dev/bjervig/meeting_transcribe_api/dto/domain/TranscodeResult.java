package dev.bjervig.meeting_transcribe_api.dto.domain;

import java.nio.file.Path;

public record TranscodeResult(
    Path outputPath,
    String outputFormat,
    long originalSizeBytes,
    long compressedSizeBytes,
    boolean wasTranscoded
) {}
