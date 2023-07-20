package com.gaspar.personalmetadata.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String extractPathToFile(String pathString) {
        Path path = Paths.get(pathString);
        return path.getParent().toString();
    }

    public static String extractFileName(String pathString) {
        Path path = Paths.get(pathString);
        return path.getFileName().toString();
    }

}
