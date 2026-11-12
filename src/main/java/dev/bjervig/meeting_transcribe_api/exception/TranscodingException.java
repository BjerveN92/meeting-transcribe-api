package dev.bjervig.meeting_transcribe_api.exception;

public class TranscodingException extends RuntimeException {

    public TranscodingException(String message) {
        super(message);
    }

    public TranscodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
