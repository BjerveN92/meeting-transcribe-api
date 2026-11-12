package dev.bjervig.meeting_transcribe_api.controller;

import dev.bjervig.meeting_transcribe_api.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("profile") String profileId,
            @RequestParam(value = "userId", defaultValue = "default")
                String userId) throws IOException {

        Path tempFile = Files.createTempFile("doc-",
            "-" + file.getOriginalFilename());
        file.transferTo(tempFile);

        try {
            ingestionService.ingest(tempFile, profileId, userId);
            return ResponseEntity.ok().build();
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
