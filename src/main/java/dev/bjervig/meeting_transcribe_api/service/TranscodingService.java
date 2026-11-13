package dev.bjervig.meeting_transcribe_api.service;

import dev.bjervig.meeting_transcribe_api.dto.domain.TranscodeResult;
import dev.bjervig.meeting_transcribe_api.exception.TranscodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class TranscodingService {

    /**
     * Format som skickas vidare utan transkodning om de är små nog. Allt annat
     * (t.ex. webm, mp4, opus i webm-container) transkodas alltid till ogg/opus.
     */
    private static final Set<String> PASSTHROUGH_FORMATS =
            Set.of("wav", "mp3", "flac", "ogg", "m4a");

    /**
     * Maxstorlek (rådata) per STT-anrop. Ljudet skickas som base64, som är ~4/3
     * av rådatan, och OpenRouter tar max 25 MB – därför 18 MB här.
     */
    @Value("${transcoding.max-size-bytes}")
    private long maxSizeBytes;

    @Value("${transcoding.bitrate}")
    private String bitrate;

    @Value("${transcoding.sample-rate}")
    private int sampleRate;

    /** Längd per bit när ett långt möte måste delas upp. */
    @Value("${transcoding.chunk-seconds}")
    private int chunkSeconds;

    public TranscodeResult process(Path inputFile) throws IOException {
        long originalSize = Files.size(inputFile);
        String ext = getExtension(inputFile);

        if (originalSize <= maxSizeBytes && PASSTHROUGH_FORMATS.contains(ext)) {
            return new TranscodeResult(List.of(inputFile), ext,
                    originalSize, originalSize, false, chunkSeconds, null);
        }

        Path workDir = Files.createTempDirectory("transcode-");
        try {
            Path transcoded = workDir.resolve("full.ogg");
            runFfmpeg("ffmpeg", "-i", inputFile.toString(),
                    "-vn",
                    "-c:a", "libopus",
                    "-b:a", bitrate,
                    "-ar", String.valueOf(sampleRate),
                    "-ac", "1",
                    "-y", transcoded.toString());

            long transcodedSize = Files.size(transcoded);
            List<Path> chunks = transcodedSize <= maxSizeBytes
                    ? List.of(transcoded)
                    : split(transcoded, workDir);

            return new TranscodeResult(chunks, "ogg", originalSize,
                    transcodedSize, true, chunkSeconds, workDir);
        } catch (IOException | RuntimeException e) {
            new TranscodeResult(List.of(), "ogg", 0, 0, true, 0, workDir).cleanup();
            throw e;
        }
    }

    /** Delar upp den transkodade filen i bitar om chunkSeconds utan omkodning. */
    private List<Path> split(Path transcoded, Path workDir) throws IOException {
        runFfmpeg("ffmpeg", "-i", transcoded.toString(),
                "-c", "copy",
                "-f", "segment",
                "-segment_time", String.valueOf(chunkSeconds),
                "-reset_timestamps", "1",
                "-y", workDir.resolve("chunk_%04d.ogg").toString());

        List<Path> chunks;
        try (Stream<Path> files = Files.list(workDir)) {
            chunks = new ArrayList<>(files
                    .filter(p -> p.getFileName().toString().startsWith("chunk_"))
                    .sorted()
                    .toList());
        }
        if (chunks.isEmpty()) {
            throw new TranscodingException("ffmpeg skapade inga ljudbitar");
        }
        for (Path chunk : chunks) {
            if (Files.size(chunk) > maxSizeBytes) {
                throw new TranscodingException(
                        "Ljudbiten %s är %d byte, över gränsen %d. Sänk transcoding.chunk-seconds eller transcoding.bitrate."
                                .formatted(chunk.getFileName(), Files.size(chunk), maxSizeBytes));
            }
        }
        return chunks;
    }

    private void runFfmpeg(String... command) throws IOException {
        try {
            int exitCode = new ProcessBuilder(command).inheritIO().start().waitFor();
            if (exitCode != 0) {
                throw new TranscodingException("ffmpeg avslutade med kod " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TranscodingException("Transkodning avbröts", e);
        }
    }

    private String getExtension(Path path) {
        String name = path.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }
}
