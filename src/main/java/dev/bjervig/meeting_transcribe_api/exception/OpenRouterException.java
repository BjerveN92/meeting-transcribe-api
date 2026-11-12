package dev.bjervig.meeting_transcribe_api.exception;

public class OpenRouterException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public OpenRouterException(int statusCode, String responseBody) {
        super("OpenRouter API fel %d: %s".formatted(statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}
