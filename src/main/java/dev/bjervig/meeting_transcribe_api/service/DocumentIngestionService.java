package dev.bjervig.meeting_transcribe_api.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    public DocumentIngestionService(
            @Autowired(required = false) VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest(Path document, String profileId, String userId) {
        if (vectorStore == null) {
            throw new IllegalStateException(
                    "VectorStore ej konfigurerad. RAG-funktionen kräver en "
                            + "embedding-modell och pgvector. Se dokumentationen.");
        }

        var reader = new TikaDocumentReader(document.toUri().toString());
        List<Document> docs = reader.get();

        var splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(200)
                .build();
        List<Document> chunks = splitter.apply(docs);

        chunks.forEach(chunk -> {
            chunk.getMetadata().put("type", "USER_DOCUMENT");
            chunk.getMetadata().put("profile", profileId);
            chunk.getMetadata().put("userId", userId);
            chunk.getMetadata().put("source",
                    document.getFileName().toString());
        });

        vectorStore.add(chunks);
    }
}
