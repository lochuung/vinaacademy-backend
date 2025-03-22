package com.vinaacademy.platform.feature.video.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FFmpegUtils {

    public static int convertToAdaptiveHLS(Path inputFilePath, Path outputBaseDir) throws IOException, InterruptedException {
        if (Files.exists(outputBaseDir)) {
            deleteDirectoryRecursively(outputBaseDir);
        }
        Files.createDirectories(outputBaseDir);

        List<VideoVariant> variants = Arrays.asList(
                new VideoVariant("480p", "854x480", "800k", "96k"),
                new VideoVariant("720p", "1280x720", "1500k", "128k"),
                new VideoVariant("1080p", "1920x1080", "3000k", "192k")
        );

        StringBuilder masterPlaylistBuilder = new StringBuilder();

        for (VideoVariant variant : variants) {
            Path variantDir = outputBaseDir.resolve(variant.name());
            Files.createDirectories(variantDir);

            int exitCode = convertToVariantHLS(inputFilePath, variant, variantDir);

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg failed for " + variant.name());
            }

            masterPlaylistBuilder.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(variant.getBandwidthEstimate()).append(",RESOLUTION=")
                    .append(variant.resolution()).append("\n")
                    .append(variant.name()).append("/playlist.m3u8\n");
        }

        // Tạo master.m3u8
        Path masterPlaylist = outputBaseDir.resolve("master.m3u8");
        String masterContent = "#EXTM3U\n" + masterPlaylistBuilder;
        Files.writeString(masterPlaylist, masterContent);

        return 0;
    }

    private static int convertToVariantHLS(Path inputFilePath, VideoVariant variant, Path variantDir) throws IOException, InterruptedException {
        String playlistPath = variantDir.resolve("playlist.m3u8").toString().replace("\\", "/");
        String segmentPattern = variantDir.resolve("segment_%03d.ts").toString().replace("\\", "/");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFilePath.toString(),
                "-vf", "scale=" + variant.resolution(),
                "-c:v", "libx264",
                "-b:v", variant.videoBitrate(),
                "-c:a", "aac",
                "-b:a", variant.audioBitrate(),
                "-hls_time", "4",
                "-hls_list_size", "0",
                "-hls_segment_filename", segmentPattern,
                "-f", "hls",
                playlistPath
        );

        pb.inheritIO();
        Process process = pb.start();
        return process.waitFor();
    }

    public static void generateThumbnail(Path inputFilePath, Path outputImagePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFilePath.toString(),
                "-ss", "00:00:02",
                "-vframes", "1",
                outputImagePath.toString()
        );
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Thumbnail generation failed");
        }
    }

    public static void deleteDirectoryRecursively(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("Delete failed: {}", p, e);
                        }
                    });
        }
    }

    private record VideoVariant(String name, String resolution, String videoBitrate, String audioBitrate) {
        public int getBandwidthEstimate() {
            // Rough estimate for bandwidth
            int videoKbps = Integer.parseInt(videoBitrate.replace("k", ""));
            int audioKbps = Integer.parseInt(audioBitrate.replace("k", ""));
            return (videoKbps + audioKbps) * 1024;
        }
    }
}
