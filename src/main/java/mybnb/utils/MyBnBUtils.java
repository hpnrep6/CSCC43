package mybnb.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public class MyBnBUtils {
    public static String getFileString(String filename) {
        try {
            return Files.readString(Path.of(filename));
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException();
        }
    }

    public static String generateStatus(String status) {
        return "{\"status\": \"" + status + "\"}";
    }
}
