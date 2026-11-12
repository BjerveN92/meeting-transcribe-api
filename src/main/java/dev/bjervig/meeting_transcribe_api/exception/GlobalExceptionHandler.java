package dev.bjervig.meeting_transcribe_api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OpenRouterException.class)
    public ResponseEntity<Map<String, String>> handleOpenRouter(OpenRouterException e) {
        return ResponseEntity
            .status(mapStatus(e.getStatusCode()))
            .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(TranscodingException.class)
    public ResponseEntity<Map<String, String>> handleTranscoding(TranscodingException e) {
        return ResponseEntity
            .status(500)
            .body(Map.of("error", "Transkodning misslyckades: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity
            .badRequest()
            .body(Map.of("error", e.getMessage()));
    }

    private int mapStatus(int openRouterStatus) {
        return switch (openRouterStatus) {
            case 401, 402 -> 502;
            case 429 -> 429;
            default -> 500;
        };
    }
}
