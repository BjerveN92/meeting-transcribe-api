package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.dto.domain.ContextChunk;
import dev.bjervig.meeting_transcribe_api.dto.domain.RelevantContext;
import dev.bjervig.meeting_transcribe_api.dto.domain.Transcription;
import dev.bjervig.meeting_transcribe_api.model.AnalysisProfile;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextService {

    private final VectorStore vectorStore;

    public ContextService(@Autowired(required = false) VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public RelevantContext retrieve(Transcription transcription,
            AnalysisProfile profile) {

        if (vectorStore == null) {
            return new RelevantContext(List.of(), profile);
        }

        String query = extractSearchQuery(transcription);

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .similarityThreshold(0.7)
                        .filterExpression(
                                "profile == '%s' || type == 'USER_DOCUMENT'"
                                        .formatted(profile.getId()))
                        .build());

        List<ContextChunk> chunks = results.stream()
                .map(doc -> new ContextChunk(
                        doc.getText(),
                        doc.getMetadata().getOrDefault("source", "okänd").toString(),
                        0.0, // score hämtas från metadata om tillgängligt
                        ContextChunk.ChunkType.valueOf(
                                doc.getMetadata().getOrDefault("type",
                                        "USER_DOCUMENT").toString())))
                .toList();

        return new RelevantContext(chunks, profile);
    }

    private String extractSearchQuery(Transcription t) {
        String raw = t.rawText();
        if (raw.length() <= 1000)
            return raw;
        return raw.substring(0, 500) + " " + raw.substring(raw.length() - 500);
    }
}
