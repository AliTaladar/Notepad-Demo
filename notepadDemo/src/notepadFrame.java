import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import editor.*;
import java.io.FileWriter;
import java.io.IOException;

public class notepadFrame extends JFrame {
    private JTextArea textArea;
    private static int openWindows = 0;
    private JLabel statusBar;
    private final JMenuItem[] editMenuItems = {new JMenuItem("Cut"), new JMenuItem("Copy"),
            new JMenuItem("Paste"), new JMenuItem("Delete")};
    private JCheckBoxMenuItem autoSaveItem;

    // Modular components
    private TextEditor textEditor;
    private FileHandler fileHandler;
    private PrintManager printManager;

    public notepadFrame(String text) {
        super("Notepad Demo");

        openWindows++;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        initializeComponents(text);
        makeMenu();
    }

    private void initializeComponents(String text) {
        // Create basic components
        textArea = new JTextArea(22, 65);
        statusBar = new JLabel();
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));

        // Initialize modular components
        textEditor = new TextEditor(textArea, statusBar);
        fileHandler = new FileHandler(this, textArea, statusBar);
        printManager = new PrintManager(this, textArea, statusBar);

        // Set up text area
        textEditor.setupTextArea();

        // Add components to frame
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane);
        add(statusBar, BorderLayout.SOUTH);

        // Add text change listener
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private boolean isInternalChange = false;

            public void insertUpdate(javax.swing.event.DocumentEvent e) { 
                if (!isInternalChange) {
                    textChanged(); 
                }
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                if (!isInternalChange) {
                    textChanged(); 
                }
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                if (!isInternalChange) {
                    textChanged(); 
                }
            }
        });

        // Set initial text if provided
        if (text != null && !text.isEmpty()) {
            textArea.setText(text);
        }

        // Add selection listener for edit menu items
        textArea.addCaretListener(e -> {
            boolean hasSelection = textArea.getSelectionStart() != textArea.getSelectionEnd();
            for (int i = 0; i < editMenuItems.length; i++) {
                if (i != 2) // Skip paste item
                    editMenuItems[i].setEnabled(hasSelection);
            }
            textEditor.updateStatistics(); // Update statistics on caret movement
        });

        // Initial statistics update
        textEditor.updateStatistics();

        pack();
    }

    private void textChanged() {
        if (!getTitle().endsWith("*")) {
            setTitle(getTitle() + "*");
        }
        
        if (fileHandler.isAutoSaveEnabled()) {
            fileHandler.autoSave();
        }
        
        textEditor.updateStatistics();
    }

    private void performAutoSave() {
        if (fileHandler.isAutoSaveEnabled()) {
            fileHandler.autoSave();
        }
    }

    private JMenu makeFileMenu() {
        JMenu file = new JMenu("File");

        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemNewWindow = new JMenuItem("New Window");
        JMenuItem itemOpen = new JMenuItem("Open...");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemSaveAs = new JMenuItem("Save As...");
        autoSaveItem = new JCheckBoxMenuItem("Auto-save");
        JMenu printMenu = new JMenu("Print");
        JMenuItem itemPageSetup = new JMenuItem("Page Setup...");
        JMenuItem itemPrintPreview = new JMenuItem("Print Preview...");
        JMenuItem itemPrint = new JMenuItem("Print...");
        JMenuItem itemExit = new JMenuItem("Exit");

        // Set up print menu items
        itemPageSetup.addActionListener(e -> printManager.showPageSetup());
        itemPrintPreview.addActionListener(e -> printManager.showPrintPreview());
        itemPrint.addActionListener(e -> printManager.print());
        itemPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));

        // Add items to print submenu
        printMenu.add(itemPageSetup);
        printMenu.add(itemPrintPreview);
        printMenu.addSeparator();
        printMenu.add(itemPrint);

        // Set up auto-save
        autoSaveItem.addActionListener(e -> {
            boolean selected = autoSaveItem.isSelected();
            
            if (selected && fileHandler.getCurrentPath() == null) {
                // If auto-save is enabled but no file is saved, prompt for save location
                if (!fileHandler.saveFileAs()) {
                    autoSaveItem.setSelected(false);
                    return;
                }
            }
            
            fileHandler.setAutoSaveEnabled(selected);
            // Re-sync the menu item with FileHandler state
            autoSaveItem.setSelected(fileHandler.isAutoSaveEnabled());
        });

        // Add action listeners
        itemNew.addActionListener(e -> newOption());
        itemNewWindow.addActionListener(e -> newWindowOption());
        itemOpen.addActionListener(e -> {
            fileHandler.openFile();
            // Sync auto-save menu item with FileHandler state
            autoSaveItem.setSelected(fileHandler.isAutoSaveEnabled());
        });
        itemSave.addActionListener(e -> fileHandler.saveFile());
        itemSaveAs.addActionListener(e -> fileHandler.saveFileAs());
        itemExit.addActionListener(e -> exitApplication());

        // Add keyboard shortcuts
        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        // Add all items to menu
        file.add(itemNew);
        file.add(itemNewWindow);
        file.add(itemOpen);
        file.addSeparator();
        file.add(itemSave);
        file.add(itemSaveAs);
        file.addSeparator();
        file.add(autoSaveItem);
        file.addSeparator();
        file.add(printMenu);
        file.addSeparator();
        file.add(itemExit);

        return file;
    }

    private JMenu makeEditMenu() {
        JMenu edit = new JMenu("Edit");

        for (int i = 0; i < editMenuItems.length; i++) {
            if (i != 2)
                editMenuItems[i].setEnabled(false);
            edit.add(editMenuItems[i]);
        }

        editMenuItems[0].addActionListener(e -> {
            copyText(textArea.getSelectedText());
            textArea.replaceSelection("");
        });

        editMenuItems[1].addActionListener(e -> copyText(textArea.getSelectedText()));
        editMenuItems[2].addActionListener(e -> textArea.paste());
        editMenuItems[3].addActionListener(e -> textArea.replaceSelection(""));

        edit.addSeparator();

        JMenuItem findReplaceItem = new JMenuItem("Find/Replace");
        findReplaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findReplaceItem.addActionListener(e -> new FindReplaceDialog(this, textArea).setVisible(true));
        edit.add(findReplaceItem);

        edit.addSeparator();

        JMenuItem timeDateItem = new JMenuItem("Time/Date");
        timeDateItem.addActionListener(e -> pasteTimeDate());
        edit.add(timeDateItem);

        return edit;
    }

    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
        menuBar.add(makeEditMenu());
        menuBar.add(makeFormatMenu());
        setJMenuBar(menuBar);
    }

    private JMenu makeFormatMenu() {
        JMenu format = new JMenu("Format");
        JMenuItem font = new JMenuItem("Font...");
        font.addActionListener(e -> openFormatter());
        format.add(font);
        return format;
    }

    private void copyText(String text) {
        if (text != null) {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(text), null);
        }
    }

    private void pasteTimeDate() {
        textArea.replaceSelection(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")));
    }

    private void openFormatter() {
        Font currentFont = textArea.getFont();
        textFormatter formatter = new textFormatter(this,
                currentFont.getName(),
                currentFont.getStyle(),
                currentFont.getSize());
        formatter.setLocationRelativeTo(this);
        formatter.setVisible(true);
    }

    public void changeFont(String fontName, int fontStyle, int fontSize) {
        textArea.setFont(new Font(fontName, fontStyle, fontSize));
    }

    private void newOption() {
        setTitle("Notepad Demo");
        textArea.setText("");
        fileHandler = new FileHandler(this, textArea, statusBar);
        pack();
    }

    private void newWindowOption() {
        notepadFrame frame = new notepadFrame("");
        frame.setTitle("Notepad Demo");
        frame.setVisible(true);
        frame.pack();
    }

    private void exitApplication() {
        if (fileHandler.isAutoSaveEnabled()) {
            cleanup();
            dispose();
            System.exit(0);
            return;
        }

        // Check if there are unsaved changes
        if (getTitle().endsWith("*")) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to save changes?",
                "Notepad Demo",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                if (fileHandler.saveFile()) {
                    cleanup();
                    dispose();
                    System.exit(0);
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                cleanup();
                dispose();
                System.exit(0);
            }
        } else {
            cleanup();
            dispose();
            System.exit(0);
        }
    }

    private void cleanup() {
        openWindows--;
        if (openWindows == 0) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        notepadFrame frame = new notepadFrame("");
        frame.setVisible(true);
        frame.pack();
    }
}
