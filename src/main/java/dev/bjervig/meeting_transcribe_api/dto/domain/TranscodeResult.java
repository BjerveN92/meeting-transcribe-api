package dev.bjervig.meeting_transcribe_api.dto.domain;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @param chunks           ljudfiler att skicka till STT, i tidsordning. En fil om
 *                         ljudet får plats i ett anrop, annars flera bitar.
 * @param chunkSeconds     längd per bit (sista biten kan vara kortare). Används för
 *                         att förskjuta tidsstämplar när bitarna slås ihop.
 * @param workDir          temporär katalog med transkodade filer, eller null om
 *                         originalfilen skickas som den är. Raderas via {@link #cleanup()}.
 */
public record TranscodeResult(
    List<Path> chunks,
    String outputFormat,
    long originalSizeBytes,
    long compressedSizeBytes,
    boolean wasTranscoded,
    int chunkSeconds,
    Path workDir
) {
    public int chunkCount() {
        return chunks.size();
    }

    /** Raderar transkodade filer. Originalfilen ägs av anroparen och lämnas kvar. */
    public void cleanup() {
        if (workDir == null) {
            return;
        }
        try (Stream<Path> paths = Files.walk(workDir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
