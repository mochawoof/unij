import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.*;
import java.util.stream.*;
import java.awt.datatransfer.StringSelection;

class Main {
    public static int charsPerRow = 16;
    public static ArrayList<SingleChar> chars = new ArrayList<SingleChar>();

    public static JFrame f;
    public static JSplitPane split;
    public static JTable table;
    public static DefaultTableModel model;
    public static JPanel topPanel;
    public static JTextField searchField;
    public static JButton searchButton;
    public static JComboBox searchMethod;
    public static JButton fontButton;
    public static JButton dataButton;
    public static JPanel sidePanel;
    public static JLabel bigChar;
    public static JButton copyButton;
    public static JTextArea charLabel;

    public static String search = "";
    public static String searchMeth = "By Contains";
    public static Font font = new Font("SansSerif", Font.PLAIN, 14);
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        f = new JFrame("UniJ 2.0");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        split.setDividerLocation(600);
        f.add(split, BorderLayout.CENTER);

        table = new JTable();
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(font);
        ListSelectionListener tableListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (table.getSelectedRow() != -1 && table.getSelectedColumn() != -1) {
                    SingleChar ch = (SingleChar) model.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                    if (ch != null) {
                        bigChar.setText(ch.toString());
                        charLabel.setText(ch.raw);
                    } else {
                        bigChar.setText("");
                        charLabel.setText("");
                    }
                }
            }
        };
        table.getSelectionModel().addListSelectionListener(tableListener);
        table.getColumnModel().getSelectionModel().addListSelectionListener(tableListener);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        split.setLeftComponent(tableScrollPane);

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        f.add(topPanel, BorderLayout.PAGE_START);

        searchField = new JTextField();
        searchField.setFont(font);
        topPanel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search = searchField.getText();
                refresh();
            }
        });
        topPanel.add(searchButton);

        searchMethod = new JComboBox(new String[] {"By Contains", "By Contains Case Sensitive"});
        searchMethod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchMeth = (String) searchMethod.getSelectedItem();
            }
        });
        topPanel.add(searchMethod);

        fontButton = new JButton("Using Font " + font.getName());
        fontButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFontChooser c = new JFontChooser(null, font);
                if (c.showDialog(f) == JFontChooser.OK_OPTION) {
                    font = c.getSelectedFont();

                    table.setFont(font);
                    searchField.setFont(font);
                    fontButton.setText("Using Font " + font.getName());
                    bigChar.setFont(new Font(font.getName(), font.getStyle(), 100));

                    refresh();
                }
            }
        });
        topPanel.add(fontButton);

        dataButton = new JButton("Using Data Built-In");
        topPanel.add(dataButton);

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        split.setRightComponent(sidePanel);

        bigChar = new JLabel();
        bigChar.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigChar.setFont(new Font(font.getName(), font.getStyle(), 100));
        sidePanel.add(bigChar);

        JButton copyButton = new JButton("Copy");
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!bigChar.getText().equals("")) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(bigChar.getText()), null);
                }
            }
        });
        sidePanel.add(copyButton);

        charLabel = new JTextArea();
        charLabel.setEditable(false);
        charLabel.setLineWrap(true);
        charLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(charLabel);

        getFromDisk();
        refresh();

        f.setVisible(true);
    }

    public static void getFromDisk() {
        chars.clear();
        for (String line : new BufferedReader(
            new InputStreamReader(
                Main.class.getResourceAsStream("UnicodeData.txt"),
                StandardCharsets.UTF_8
            )
        ).lines().collect(Collectors.joining("\n")).split("\n")) {
            String[] splitLine = line.split(";");
            try {
                int code = Integer.parseInt(splitLine[0], 16);
                chars.add(new SingleChar(line, code));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void refresh() {
        bigChar.setText("");
        charLabel.setText("");

        model = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) {return false;}

            public String getColumnName(int col) {
                return "" + col;
            }
        };
        model.setColumnCount(charsPerRow);
        model.setRowCount((int) Math.ceil((double) chars.size() / charsPerRow));
        table.setModel(model);

        int n = 0;
        for (int i = 0; i < chars.size(); i++) {
            int row = n / charsPerRow;
            int col = n - (row * charsPerRow);
            SingleChar ch = chars.get(i);

            if (!search.equals("")) {
                if (searchMeth.equals("By Contains")) {
                    if (!ch.raw.toUpperCase().contains(search.toUpperCase())) {
                        continue;
                    }
                } else if (searchMeth.equals("By Contains Case Sensitive")) {
                    if (!ch.raw.contains(search)) {
                        continue;
                    }
                }
            }

            model.setValueAt(ch, row, col);
            n++;
        }
    }
}