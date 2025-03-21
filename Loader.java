import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.stream.*;
import java.nio.charset.*;
import java.util.HashMap;

class Loader {
    public static HashMap<Integer, String> data = new HashMap<Integer, String>();
    public static int lastCode;
    public static File file;
    private static String getStr(File file) {
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
    public static void loadData(File fl, Runnable cb) {
        file = fl;
        data.clear();
        new Thread() {
            public void run() {
                String s = getStr(fl);
                String[] lines = s.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    try {
                        int code = (int) Integer.decode("0x" + line.split(";")[0]);
                        lastCode = code;
                        data.put(code, line);
                    } catch (Exception e) {
                        System.err.println("Invalid line " + i + " in " + file.getName());
                        System.err.println(e.toString());
                    }
                }
                cb.run();
            }
        }.start();
    }
}