import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class notepadFrame extends JFrame {
    private JTextArea textArea;
    private String path = null;
    boolean changesMade = false;
    private static int openWindows = 0;
    private JLabel statusBar;  
    private javax.swing.Timer statsUpdateTimer;  

    private final JMenuItem[] editMenuItems = {new JMenuItem("Cut"), new JMenuItem("Copy"),
            new JMenuItem("Paste"), new JMenuItem("Delete")};

    /**
     * @param text argument passed to the function makeTextArea()
     */
    public notepadFrame(String text) {
        super("Notepad Demo");

        openWindows++;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitOption();
            }
        });

        makeTextArea(text);
        makeMenu();

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        // Create and add status bar
        statusBar = new JLabel();
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        add(statusBar, BorderLayout.SOUTH);

        // Initialize timer to update statistics every 1000 milliseconds
        statsUpdateTimer = new javax.swing.Timer(1000, e -> updateStatistics());
        statsUpdateTimer.start();
    }

    /**
     * Initializes the JTextArea and adds attributes to it
     *
     * @param text the initial text in the text area
     */
    private void makeTextArea(String text) {
        textArea = new JTextArea(22, 65);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(text);
        textArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!changesMade) {
                    changesMade = true;
                    setTitle(getTitle() + "*");
                }
                updateStatistics(); // Update statistics on key press
            }
        });

        textArea.addCaretListener(e -> {
            int length = textArea.getSelectionEnd() - textArea.getSelectionStart();
            if (length > 0) {
                for (int i = 0; i < editMenuItems.length; i++) {
                    if (i != 2)
                        editMenuItems[i].setEnabled(true);
                }
            } else {
                if (editMenuItems[0].isEnabled()) {
                    for (int i = 0; i < editMenuItems.length; i++) {
                        if (i != 2)
                            editMenuItems[i].setEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * Makes the main JMenuBar
     */
    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
        menuBar.add(makeEditMenu());
        menuBar.add(makeFormatMenu());

        setJMenuBar(menuBar);
    }

    /**
     * Makes the file menu with all its items
     *
     * @return file menu
     */
    private JMenu makeFileMenu() {
        JMenu file = new JMenu("File");

        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemNewWindow = new JMenuItem("New Window");
        JMenuItem itemOpen = new JMenuItem("Open...");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemSaveAs = new JMenuItem("Save As...");
        JMenuItem itemExit = new JMenuItem("Exit");

        file.add(itemNew);
        file.add(itemNewWindow);
        file.add(itemOpen);
        file.addSeparator();
        file.add(itemSave);
        file.add(itemSaveAs);
        file.addSeparator();
        file.add(itemExit);

        itemExit.addActionListener(e -> exitOption());

        itemOpen.addActionListener(e -> openOption());

        itemNew.addActionListener(e -> newOption());

        itemNewWindow.addActionListener(e -> newWindowOption());

        itemSave.addActionListener(e -> optionSave());

        itemSaveAs.addActionListener(e -> {
            if (optionSaveAs()) {
                changesMade = false;
            }
        });

        return file;
    }

    /**
     * Makes the edit menu with all its items
     *
     * @return edit menu
     */
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

        JMenuItem timeDateItem = new JMenuItem("Time/Date");

        timeDateItem.addActionListener(e -> pasteTimeDate());

        edit.add(timeDateItem);

        return edit;
    }

    /**
     * Makes the format menu with all its items
     *
     * @return format menu
     */
    private JMenu makeFormatMenu() {
        JMenu format = new JMenu("Format");

        JMenuItem font = new JMenuItem("Font...");

        font.addActionListener(e -> openFormatter());

        format.add(font);

        return format;
    }

    /**
     * opens the formatter dialog
     */
    private void openFormatter() {
        System.out.println(textArea.getFont().getSize());
        textFormatter formatter = new textFormatter(this, textArea.getFont().getName(),
                textArea.getFont().getStyle(), textArea.getFont().getSize());
        formatter.setVisible(true);
        formatter.pack();
        formatter.setLocationRelativeTo(null);
    }

    /**
     * Changes the font of the the text area
     * @param fontName the name of the font in the computer
     * @param fontStyle the integer representing the style of the font (Regular, Bold, Italic, Bold Italic)
     * @param fontSize the integer representing the size of the font
     */
    public void changeFont(String fontName, int fontStyle, int fontSize) {
        Font font = new Font(fontName, fontStyle, fontSize);
        textArea.setFont(font);
        System.out.println(textArea.getFont().getName());

    }

    /**
     * Pastes the dateTime on the text area
     */
    private void pasteTimeDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:m a yyyy-MM-dd");
        textArea.setText(textArea.getText() + dtf.format(localDateTime));
    }

    /**
     * Copies the selected text to the clipboard
     * @param selectedText the highlighted text
     */
    private void copyText(String selectedText) {
        StringSelection selection = new StringSelection(selectedText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    /**
     * Exits the program
     */
    private void exitOption() {
        if (!changesMade) {
            cleanup();
        } else {
            int confirmed = showSaveDialog();

            if (confirmed == 0) {
                if (optionSave()) {
                    cleanup();
                }
            } else if (confirmed == 1) {
                cleanup();
            }
        }
    }

    /**
     * Cleanup resources when closing the window
     */
    private void cleanup() {
        if (statsUpdateTimer != null) {
            statsUpdateTimer.stop();
        }
        openWindows--;
        if (openWindows == 0) {
            System.exit(0);
        }
        dispose();
    }

    /**
     * Shows the save pop-up dialog
     * @return the option chosen by the user
     */
    private int showSaveDialog() {
        String[] options = {"Save", "Don't save", "Cancel"};
        return JOptionPane.showOptionDialog(notepadFrame.this,
                "Save the changes?",
                "Exit...",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
    }

    /**
     * Allows to open a text file on the computer
     */
    private void openOption() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:/Users/"));
        fileChooser.setDialogTitle("Select a folder...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        String path = fileChooser.getSelectedFile().getAbsolutePath();
        String fileName = fileChooser.getSelectedFile().getName();

        if (changesMade) {
            int confirmed = showSaveDialog();

            if (confirmed == 0) {
                if (optionSave()) {
                    openWindow(path, fileName);
                }
            } else if (confirmed == 1) {
                openWindow(path, fileName);
            }
        } else {
            openWindow(path, fileName);
        }
    }

    /**
     * "opens" the window of the new text file
     * @param path The path to the opened text file
     * @param fileName the name of the text file
     */
    private void openWindow(String path, String fileName) {
        textArea.setText(readTextFile(path));
        this.path = path;
        changesMade = false;
        setTitle(fileName);
    }

    /**
     * reads a text file given its path
     * @param path the path to the text file
     * @return a string containing the text inside the text file
     */
    private String readTextFile(String path) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * Allows to save a file in a specific location in the computer
     * @return a boolean showing whether or not the file has been saved
     */
    private boolean optionSaveAs() {
        String fileName;
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("C:/Users/alita/Desktop"));
            fileChooser.setDialogTitle("Select a folder...");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showOpenDialog(null);

            String fileChooserPath = fileChooser.getSelectedFile().getAbsolutePath();

            fileName = fileNameChooser();

            if (fileName == null || fileName.equals("/.txt")) {
                JOptionPane.showMessageDialog(notepadFrame.this,
                        "File Not Saved!\nYou did not enter a name for your file!",
                        "File Not Saved!",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            FileWriter writer = new FileWriter(fileChooserPath + fileName);
            writer.write(textArea.getText());
            this.path = fileChooserPath + fileName;
            writer.close();
        } catch (NullPointerException e) {
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(notepadFrame.this,
                    "Error!",
                    "Error!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        setTitle(fileName.substring(1));
        return true;
    }

    /**
     * Allows the user to choose a name for their text file
     * @return the string containing the name of the text file with its extension
     */
    private String fileNameChooser() {
        try {
            Object fileName = JOptionPane.showInputDialog(notepadFrame.this,
                    "Choose a name for your text file...", "Choose a name",
                    JOptionPane.PLAIN_MESSAGE);
            if (fileName == null) {
                return null;
            }
            return "/" + fileName + ".txt";
        } catch (NullPointerException e) {
            return null;
        }

    }

    /**
     * Allows to save a file
     * @return a boolean showing whether or not the file has been saved
     */
    private boolean optionSave() {
        if (path == null) {
            boolean saveConfirmation = optionSaveAs();
            if (saveConfirmation) {
                changesMade = false;
            }
            return saveConfirmation;
        }
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(textArea.getText());
            writer.close();
            if (changesMade) {
                setTitle(getTitle().substring(0, getTitle().length() - 1));
                changesMade = false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Opens a new window
     * This method does not close the previous window
     */
    public void newWindowOption() {
        notepadFrame frame = new notepadFrame("");
        frame.setVisible(true);
        frame.pack();
    }

    /**
     * Clears the current window
     * Asks the user if they want to save the changes (if applicable)
     */
    public void newOption() {
        if (!changesMade) {
            textArea.setText("");
            changesMade = false;
            setTitle("Notepad");
            return;
        }
        String[] options = {"Save", "Don't save", "Cancel"};
        int confirmed = JOptionPane.showOptionDialog(notepadFrame.this,
                "Save the changes?",
                "New...",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        if (confirmed == 0) {
            if (optionSave()) {
                textArea.setText("");
                changesMade = false;
                setTitle("Notepad");
            }
        } else if (confirmed == 1) {
            textArea.setText("");
            changesMade = false;
            setTitle("Notepad");
        }
    }

    /**
     * Updates the status bar with current text statistics
     */
    private void updateStatistics() {
        String text = textArea.getText();
        int chars = text.length();
        int charsNoSpaces = text.replaceAll("\\s+", "").length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lines = textArea.getLineCount();

        try {
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos) + 1;
            int colNum = caretPos - textArea.getLineStartOffset(textArea.getLineOfOffset(caretPos)) + 1;

            statusBar.setText(String.format("Line: %d  Column: %d  |  Words: %d  |  Characters: %d  |  Characters (no spaces): %d  |  Lines: %d",
                    lineNum, colNum, words, chars, charsNoSpaces, lines));
        } catch (Exception e) {
            statusBar.setText(String.format("Words: %d  |  Characters: %d  |  Characters (no spaces): %d  |  Lines: %d",
                    words, chars, charsNoSpaces, lines));
        }
    }
}
