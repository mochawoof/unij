import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.stream.*;
import java.nio.charset.*;

class Loader {
    public static String getData(File file) {
        try {
            if (file.getName().equals("Built In")) {
                return new BufferedReader(new InputStreamReader(Loader.class.getResourceAsStream("builtin.txt"), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}