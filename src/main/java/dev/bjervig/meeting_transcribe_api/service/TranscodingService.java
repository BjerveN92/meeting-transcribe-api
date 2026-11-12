package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.dto.domain.TranscodeResult;
import dev.bjervig.meeting_transcribe_api.exception.TranscodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TranscodingService {

    @Value("${transcoding.max-size-bytes}")
    private long maxSizeBytes;

    @Value("${transcoding.bitrate}")
    private String bitrate;

    @Value("${transcoding.sample-rate}")
    private int sampleRate;

    public TranscodeResult process(Path inputFile) throws IOException {
        long originalSize = Files.size(inputFile);

        if (originalSize <= maxSizeBytes) {
            String ext = getExtension(inputFile);
            return new TranscodeResult(
                inputFile, ext, originalSize, originalSize, false);
        }

        Path output = inputFile.resolveSibling(
            "transcoded_" + stem(inputFile) + ".ogg");

        try {
            int exitCode = new ProcessBuilder(
                "ffmpeg", "-i", inputFile.toString(),
                "-c:a", "libopus",
                "-b:a", bitrate,
                "-ar", String.valueOf(sampleRate),
                "-ac", "1",
                "-y", output.toString()
            ).inheritIO().start().waitFor();

            if (exitCode != 0) {
                throw new TranscodingException(
                    "ffmpeg avslutade med kod " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TranscodingException("Transkodning avbröts", e);
        }

        return new TranscodeResult(
            output, "ogg", originalSize, Files.size(output), true);
    }

    private String getExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1) : "wav";
    }

    private String stem(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
