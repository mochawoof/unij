import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.awt.datatransfer.*;

class Main {
    private static File file = new File("Built In");
    private static String[] ldata = Loader.getData(file).split("\n");
    private static Font font = new Font("SansSerif", Font.PLAIN, 14);
    private static int charsPerRow = 16;
    private static String searchString = "";
    private static String searchMeth = "By Contains";
    
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
        
        JPanel bigletterpanel = new JPanel();
        bigletterpanel.setLayout(new BoxLayout(bigletterpanel, BoxLayout.Y_AXIS));
        preview.setTopComponent(bigletterpanel);
        
        JLabel bigletter = new JLabel("");
        bigletter.setFont(new Font(font.getName(), font.getStyle(), 100));
        bigletter.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        bigletter.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigletter.setMinimumSize(new Dimension(50, 50));
        bigletterpanel.add(bigletter);
        
        JLabel biglettersizeLabel = new JLabel("100pt");
        biglettersizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigletterpanel.add(biglettersizeLabel);
        
        JButton copy = new JButton("Copy");
        copy.setAlignmentX(Component.CENTER_ALIGNMENT);
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(bigletter.getText()), null);
            }
        });
        bigletterpanel.add(copy);
        
        // Big letter size listener
        bigletter.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                bigletter.setFont(new Font(font.getName(), font.getStyle(), bigletter.getHeight()));
                biglettersizeLabel.setText(bigletter.getFont().getSize() + "pt");
            }
        });
        
        JTextArea letterlabel = new JTextArea("");
        letterlabel.setLineWrap(true);
        letterlabel.setEditable(false);
        letterlabel.setMinimumSize(new Dimension(0, 0));
        letterlabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        preview.setBottomComponent(letterlabel);
        
        ArrayList<ArrayList<String>> charData = new ArrayList<ArrayList<String>>();
        
        // Add listener to charTable
        ListSelectionListener ls = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int row = charTable.getSelectedRow();
                int col = charTable.getSelectedColumn();
                if (row != -1 && col != -1) {
                    String ln = null;
                    String c = null;
                    for (String l : ldata) {
                        String ch = l.split(";")[0];
                        try {
                            if (ch.equals(charData.get(row).get(col))) {
                                ln = l;
                                //c = new String(new char[] {(char) ((int) Integer.decode("0x" + ln.split(";")[0]))});
                                c = new String(Character.toChars((int) Integer.decode("0x" + ln.split(";")[0])));
                                break;
                            }
                        } catch (Exception ex) {
                            break;
                        }
                    }
                    if (ln != null && c != null) {
                        bigletter.setText(c);
                        letterlabel.setText(ln);
                    } else {
                        bigletter.setText("");
                        letterlabel.setText("");
                    }
                }
            }
        };
        charTable.getSelectionModel().addListSelectionListener(ls);
        charTable.getColumnModel().getSelectionModel().addListSelectionListener(ls);
        
        JToolBar tb = new JToolBar();
        f.add(tb, BorderLayout.PAGE_END);
        
        JComponent func = new JComponent() {
            public void paint(Graphics g) {
                DefaultTableModel model = new DefaultTableModel() {
                    public boolean isCellEditable(int row, int column) {return false;}
                };
                model.setColumnCount(charsPerRow);
                charTable.setModel(model);
                
                charData.clear();
                
                for (int i = 0; i < charsPerRow; i++) {
                    charTable.getColumnModel().getColumn(i).setHeaderValue(String.format("%02X", i));
                }
                charTable.setFont(font);
                search.setFont(font);
                
                bigletter.setText("");
                bigletter.setFont(new Font(font.getName(), font.getStyle(), 100));
                letterlabel.setText("");
                
                int n = 0;
                for (int i = 0; i < ldata.length; i++) {
                    try {
                        boolean searchPassed = true;
                        if (!searchString.equals("")) {
                            searchPassed = false;
                            if (searchMeth.equals("By Contains")) {
                                searchPassed = ldata[i].toUpperCase().contains(searchString.toUpperCase());
                            } else if (searchMeth.equals("By Contains Case Sensitive")) {
                                searchPassed = ldata[i].contains(searchString);
                            } else if (searchMeth.equals("By Regex")) {
                                searchPassed = ldata[i].matches(searchString);
                            } else if (searchMeth.equals("By Character")) {
                                searchPassed = searchString.contains(new String(Character.toChars((int) Integer.decode("0x" + ldata[i].split(";")[0]))));/*new String(new char[] {(char) ((int) Integer.decode("0x" + ldata[i].split(";")[0]))}));*/
                            }
                        }
                        if (searchPassed) {
                            //String c = new String(new char[] {(char) ((int) Integer.decode("0x" + ldata[i].split(";")[0]))});
                            String c = new String(Character.toChars((int) Integer.decode("0x" + ldata[i].split(";")[0])));
                            
                            if (n % charsPerRow == 0) {
                                model.setRowCount(model.getRowCount() + 1);
                                charData.add(new ArrayList<String>());
                            }
                            model.setValueAt(c, model.getRowCount() - 1, n % charsPerRow);
                            charData.get(model.getRowCount() - 1).add(n % charsPerRow, ldata[i].split(";")[0]);
                            n++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                tabbed.setTitleAt(0, "Chars (" + n + ")");
                charTable.revalidate();
                charTable.repaint();
            }
        };
        
        // Search listener add
        searchGo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchString = search.getText();
                searchMeth = (String) searchMethod.getSelectedItem();
                func.paint(null);
            }
        });
        
        JButton loaded = new JButton("Unicode " + file.getName());
        loaded.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser c = new JFileChooser();
                
                if (file.getParent() != null) {
                    c.setCurrentDirectory(new File(file.getParent()));
                }
                
                if (c.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
                    file = c.getSelectedFile();
                    loaded.setText("Unicode " + file.getName());
                    ldata = Loader.getData(file).split("\n");
                    search.setText("");
                    func.paint(null);
                }
            }
        });
        tb.add(loaded);
        
        JButton fontbutton = new JButton("Font " + font.getName());
        fontbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFontChooser c = new JFontChooser(null, font);
                if (c.showDialog(f) == JFontChooser.OK_OPTION) {
                    font = c.getSelectedFont();
                    fontbutton.setText("Font " + font.getName());
                    func.paint(null);
                }
            }
        });
        tb.add(fontbutton);
        
        tb.add(Box.createGlue());
        JButton help = new JButton("?");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(f, "UniJ, the free cross-platform Unicode font inspector.\n\nVersion 1.0\nJava " + System.getProperty("java.version") + " " + System.getProperty("java.vendor") + "\n\nhttps://github.com/mochawoof/unij", "About UniJ", JOptionPane.PLAIN_MESSAGE, new ImageIcon(f.getIconImage()));
            }
        });
        tb.add(help);
        
        func.paint(null);
        f.setVisible(true);
    }
}