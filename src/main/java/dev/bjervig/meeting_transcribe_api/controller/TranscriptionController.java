package dev.bjervig.meeting_transcribe_api.controller;

import dev.bjervig.meeting_transcribe_api.dto.api.TranscribeResponse;
import dev.bjervig.meeting_transcribe_api.dto.domain.MeetingReport;
import dev.bjervig.meeting_transcribe_api.model.AnalysisProfile;
import dev.bjervig.meeting_transcribe_api.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TranscriptionController {

        private final MainService mainService;

        @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<TranscribeResponse> transcribe(
                        @RequestPart("file") MultipartFile file,
                        @RequestParam(value = "profile", defaultValue = "MEETING_MINUTES") String profileName,
                        @RequestParam(value = "language", required = false) String language) throws IOException {

                AnalysisProfile profile = AnalysisProfile.valueOf(
                                profileName.toUpperCase());

                Path tempFile = Files.createTempFile("meeting-",
                                "-" + file.getOriginalFilename());
                file.transferTo(tempFile);

                try {
                        MeetingReport report = mainService.process(
                                        tempFile, profile, language);
                        return ResponseEntity.ok(TranscribeResponse.from(report));
                } finally {
                        Files.deleteIfExists(tempFile);
                }
        }

        @GetMapping("/profiles")
        public ResponseEntity<List<Map<String, Object>>> getProfiles() {
                var profiles = Arrays.stream(AnalysisProfile.values())
                                .map(p -> Map.<String, Object>of(
                                                "name", p.name(),
                                                "id", p.getId(),
                                                "temperature", p.getTemperature()))
                                .toList();
                return ResponseEntity.ok(profiles);
        }
}
