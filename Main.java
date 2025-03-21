import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import java.util.*;

class Main {
    private static Font font = new Font("Dialog", Font.PLAIN, 14);
    private static int charsPerRow = 16;
    private static String searchString = "";
    private static String searchMeth = "By Contains";
    private static ArrayList<Integer> displayedChars = new ArrayList<Integer>();
    
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame f = new JFrame("UniJ");
        f.setSize(700, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            f.setIconImage(new ImageIcon(Main.class.getResource("icon64.png").toURI().toURL()).getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        f.add(searchPanel, BorderLayout.PAGE_START);
        
        JTextField search = new JTextField();
        searchPanel.add(search);
        
        JButton searchGo = new JButton("Search");
        searchPanel.add(searchGo);
        
        JComboBox searchMethod = new JComboBox(new String[] {"By Contains", "By Contains Case Sensitive", "By Regex", "By Character"});
        searchPanel.add(searchMethod);
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        split.setDividerLocation(500);
        f.add(split, BorderLayout.CENTER);
        
        JTabbedPane tabbed = new JTabbedPane();
        split.setLeftComponent(tabbed);
        
        JTable charTable = new JTable();
        charTable.setCellSelectionEnabled(true);
        JScrollPane charScroll = new JScrollPane(charTable);
        charScroll.getVerticalScrollBar().setUnitIncrement(20);
        tabbed.addTab("Chars", charScroll);
        
        JSplitPane preview = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        preview.setDividerLocation(200);
        split.setRightComponent(preview);
        
        JPanel bigLetterPanel = new JPanel();
        bigLetterPanel.setLayout(new BoxLayout(bigLetterPanel, BoxLayout.Y_AXIS));
        preview.setTopComponent(bigLetterPanel);
        
        JLabel bigLetter = new JLabel("");
        bigLetter.setFont(new Font(font.getName(), font.getStyle(), 100));
        bigLetter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        bigLetter.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigLetter.setMinimumSize(new Dimension(50, 50));
        bigLetterPanel.add(bigLetter);
        
        JLabel bigLetterSizeLabel = new JLabel("100pt");
        bigLetterSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigLetterPanel.add(bigLetterSizeLabel);
        
        JButton copy = new JButton("Copy");
        copy.setAlignmentX(Component.CENTER_ALIGNMENT);
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(bigletter.getText()), null);
            }
        });
        bigLetterPanel.add(copy);
        
        // Big letter size listener
        bigLetterPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                
            }
        });
        
        JTextArea letterLabel = new JTextArea("");
        letterLabel.setLineWrap(true);
        letterLabel.setEditable(false);
        letterLabel.setMinimumSize(new Dimension(0, 0));
        letterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        preview.setBottomComponent(letterLabel);
        
        // Add listener to charTable
        ListSelectionListener ls = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = charTable.getSelectedRow();
                int col = charTable.getSelectedColumn();
                if (row != -1 && col != -1) {
                    CharCell value = (CharCell) charTable.getModel().getValueAt(row, col);
                    if (value != null) {
                        bigLetter.setText(value.display);
                        letterLabel.setText(value.line);
                    } else {
                        bigLetter.setText("");
                        letterLabel.setText("");
                    }
                }
            }
        };
        charTable.getSelectionModel().addListSelectionListener(ls);
        charTable.getColumnModel().getSelectionModel().addListSelectionListener(ls);
        
        JToolBar tb = new JToolBar();
        f.add(tb, BorderLayout.PAGE_END);
        
        JButton loaded = new JButton("Unicode ");
        tb.add(loaded);
        
        JButton fontbutton = new JButton("Font " + font.getName());
        tb.add(fontbutton);
        
        tb.add(Box.createGlue());
        JButton help = new JButton("?");
        tb.add(help);
        
        JComponent func = new JComponent() {
            public void revalidate() {
                Loader.loadData(Loader.file, new Runnable() {
                    public void run() {
                        repaint();
                    }
                });
            }
            public void repaint() {
                bigLetter.setText("");
                letterLabel.setText("");
                loaded.setText("Unicode " + Loader.file.getName());
                displayedChars.clear();
                
                DefaultTableModel model = new DefaultTableModel() {
                    public boolean isCellEditable(int row, int col) {return false;}
                };
                model.setColumnCount(charsPerRow);
                model.setRowCount((int) Math.ceil((double) Loader.lastCode / charsPerRow));
                
                String[] columnIdentifiers = new String[charsPerRow];
                for (int i = 0; i < charsPerRow; i++) {
                    columnIdentifiers[i] = String.format("%02X", i);
                }
                model.setColumnIdentifiers(columnIdentifiers);
                
                charTable.setModel(model);
                
                int n = 0;
                System.out.println("Displaying data with range " + Loader.lastCode + " real size " + Loader.data.size());
                for (int i = 0; i < Loader.lastCode; i++) {
                    try {
                        String line = Loader.data.get(i);
                        
                        // Make sure char exists
                        if (line == null) {
                            continue;
                        }
                        
                        int code = (int) Integer.decode("0x" + line.split(";")[0]);
                        
                        // Search pass
                        if (searchString.equals("")) {
                            if (searchMeth.equals("Contains")) {
                                if (!line.contains(searchString)) {
                                    continue;
                                }
                            }
                        }
                        
                        model.setValueAt(new CharCell(code, line, new String(Character.toChars(code))), i - (model.getRowCount() * (charsPerRow - 1)), i - (charsPerRow * (model.getRowCount() - 1)));
                        displayedChars.add(code);
                        n++;
                    } catch (Exception ex) {
                        System.err.println("Table population error at " + i + " n " + n);
                        System.err.println(ex.toString());
                    }
                    i++;
                }
                System.out.println("Done");
                tabbed.setTitleAt(0, "Chars (" + n + ")");
            }
        };
        
        // Search listener add
        searchGo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        loaded.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        fontbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f, "UniJ, the free cross-platform Unicode font inspector.\n\nVersion 1.1\nJava " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n\nhttps://github.com/mochawoof/unij", "About UniJ", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
            }
        });
        
        Loader.file = new File("Built In");
        func.revalidate();
        f.setVisible(true);
    }
}