import java.io.File;
import java.nio.file.*;

class Loader {
    public static String getData(File file) {
        try {
            if (file.getName().equals("Built In")) {
                return new String(Files.readAllBytes(Paths.get(Loader.class.getResource("builtin.txt").toURI())));
            }
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}