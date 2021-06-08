import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class notepadFrame extends JFrame {
    private final JTextArea textArea;
    private String path = null;
    boolean changesMade = false;
    private static int openWindows = 0;

    private JMenuItem[] editMenuItems = {new JMenuItem("Cut"), new JMenuItem("Copy"),
            new JMenuItem("Paste"), new JMenuItem("Delete")};

    public notepadFrame(String text) {
        super("Notepad");

        openWindows++;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        textArea = new JTextArea(26, 95);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(text);


        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!changesMade) {
                    changesMade = true;
                    setTitle(getTitle() + "*");
                }
            }
        });

        textArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
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
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitOption();
            }
        });

        makeMenu();

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

    }

    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(makeFileMenu());
        menuBar.add(makeEditMenu());
        menuBar.add(makeFormatMenu());

        setJMenuBar(menuBar);
    }

    private JMenu makeFileMenu() {
        JMenu file = new JMenu("File");

        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemNewWindow = new JMenuItem("New Window");
        JMenuItem itemOpen = new JMenuItem("Open...");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemSaveAs = new JMenuItem("Save As...");
        JMenuItem itemPrint = new JMenuItem("Print");
        JMenuItem itemExit = new JMenuItem("Exit");

        file.add(itemNew);
        file.add(itemNewWindow);
        file.add(itemOpen);
        file.add(itemSave);
        file.add(itemSaveAs);
        file.addSeparator();
        file.add(itemPrint);
        file.addSeparator();
        file.add(itemExit);

        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitOption();
            }
        });

        itemOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOption();
            }
        });

        itemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newOption();
            }
        });

        itemNewWindow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newWindowOption();
            }
        });


        itemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionSave();
            }
        });

        itemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (optionSaveAs()) {
                    changesMade = false;
                }
            }
        });

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

        editMenuItems[1].addActionListener(e -> {
            copyText(textArea.getSelectedText());
        });

        editMenuItems[2].addActionListener(e -> {
            textArea.paste();
        });

        editMenuItems[3].addActionListener(e -> {
            textArea.replaceSelection("");
        });

        edit.addSeparator();

        JMenuItem timeDateItem = new JMenuItem("Time/Date");

        timeDateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteTimeDate();
            }
        });

        edit.add(timeDateItem);

        return edit;
    }

    private JMenu makeFormatMenu() {
        JMenu format = new JMenu("Format");

        JMenuItem font = new JMenuItem("Font...");

        font.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFormatter();
            }
        });

        format.add(font);

        return format;
    }

    private void openFormatter(){
        textFormatter formatter = new textFormatter();
        formatter.setVisible(true);
        formatter.pack();
        formatter.setLocationRelativeTo(null);
    }

    public void changeFont(Font font){
        textArea.setFont(font);
    }

    private void pasteTimeDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:m a YYYY-MM-dd");
        textArea.setText(textArea.getText() + dtf.format(localDateTime));
    }

    private void copyText(String selectedText) {
        StringSelection selection = new StringSelection(selectedText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }


    private void exitOption() {
        if (!changesMade) {
            closeWindow();
        } else {
            int confirmed = showSaveDialogue();

            if (confirmed == 0) {
                if (optionSave()) {
                    closeWindow();
                }
            } else if (confirmed == 1) {
                closeWindow();
            }
        }
    }

    private void closeWindow() {
        dispose();
        openWindows--;
        if (openWindows == 0)
            System.exit(0);
    }

    private int showSaveDialogue() {
        String[] options = {"Save", "Don't save", "Cancel"};
        return JOptionPane.showOptionDialog(notepadFrame.this,
                "Save the changes?",
                "Exit...",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
    }

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
            int confirmed = showSaveDialogue();

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

    private void openWindow(String path, String fileName) {
        textArea.setText(readTextFile(path));
        this.path = path;
        changesMade = false;
        setTitle(fileName);
    }

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

            File file = new File(fileChooserPath);

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

    private boolean checkFilesInDirectory(File file, String fileName) {
        fileName = fileName.substring(1);
        String[] contents = file.list();
        for (String content : contents) {
            if (content.equals(fileName)) return true;
        }
        return false;
    }

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

    public void newWindowOption() {
        notepadFrame frame = new notepadFrame("");
        frame.setVisible(true);
        frame.pack();
    }

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
}
