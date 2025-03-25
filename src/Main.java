import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.*;
import java.util.HashMap;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.*;
import java.util.stream.*;

class Main {
    public static int charsPerRow = 16;
    public static SingleChar[] chars;

    public static JFrame f;
    public static JSplitPane split;
    public static JTable table;
    public static DefaultTableModel model;
    public static void main(String[] args) {
        f = new JFrame("UniJ");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        split.setDividerLocation(700);
        f.add(split, BorderLayout.CENTER);

        table = new JTable();
        table.setCellSelectionEnabled(true);
        split.setLeftComponent(table);

        getFromDisk();
        refresh();

        f.setVisible(true);
    }

    public static void getFromDisk() {
        for (String line : new BufferedReader(
            new InputStreamReader(
                Main.class.getResourceAsStream("UnicodeData.txt"),
                StandardCharsets.UTF_8
            )
        ).lines().collect(Collectors.joining("\n")).split("\n")) {
            String[] splitLine = line.split(";");
            try {
                int code = Integer.parseInt(splitLine[0], 16);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void refresh() {
        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) {return false;}
        };
        model.setColumnCount(charsPerRow);
        table.setModel(model);

        
    }
}