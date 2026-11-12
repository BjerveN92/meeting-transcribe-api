package dev.bjervig.meeting_transcribe_api.dto.domain;

import java.util.List;

public record MeetingSummary(
    String correctedText,
    List<String> bulletPoints,
    List<String> actionItems,
    List<String> decisions,
    List<String> participants
) {}
