package com.gaspar.personalmetadata.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    private static final HashFunction MURMUR_HASHER = Hashing.murmur3_128();

    public static String extractPathToFile(Path file) {
        return file.getParent().toString();
    }

    public static String extractFileName(Path file) {
        return file.getFileName().toString();
    }

    public static String getContentType(Path file) {
        try {
            String contentType = Files.probeContentType(file);
            if(contentType == null) {
                throw new RuntimeException("Content type was null");
            }
            return contentType;
        } catch (Exception e) {
            log.error("Failed to extract file content type of '{}'", file, e);
            return "unknown";
        }
    }

    public static Image resizeImage(Image image, int size) {
        return image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
    }

    public static String fileHash(Path file) {
        try {
            byte[] fileBytes = Files.readAllBytes(file);
            return MURMUR_HASHER.hashBytes(fileBytes).toString();
        } catch (Exception e) {
            log.error("Failed to hash the file '{}'", file, e);
            return "failed";
        }
    }
}
