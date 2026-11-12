package dev.bjervig.meeting_transcribe_api.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatRequest(
    String model,
    double temperature,
    List<Message> messages
) {
    public record Message(String role, String content) {}
}
