package com.vinaacademy.platform.feature.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@UtilityClass
@Slf4j
public class FileUtils {

    public static void deleteFile(String path) throws IOException {
        Path filePath = Path.of(path);
        deleteFile(filePath);
    }

    public static void deleteFile(Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            Files.walk(filePath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        }
    }
}
