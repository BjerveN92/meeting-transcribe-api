package dev.bjervig.meeting_transcribe_api.dto.domain;

public record ContextChunk(
    String content,
    String sourceDocument,
    double similarityScore,
    ChunkType type
) {
    public enum ChunkType {
        MEETING_TYPE_GUIDE,
        USER_DOCUMENT
    }
}
