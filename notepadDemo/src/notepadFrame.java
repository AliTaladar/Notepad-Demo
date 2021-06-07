import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.*;


public class notepadFrame extends JFrame {
    private final JTextArea textArea;
    private String path = null;
    boolean changesMade = false;
    private static int openWindows = 0;

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

        return edit;
    }

    private JMenu makeFormatMenu() {
        JMenu format = new JMenu("Format");

        return format;
    }

    private void exitOption() {
        if (!changesMade) {
            dispose();
            openWindows--;
            if (openWindows == 0)
                System.exit(0);
        }
        int confirmed = showSaveDialogue();

        if (confirmed == 0) {
            if (optionSave()) {
                dispose();
                openWindows--;
                if (openWindows == 0)
                    System.exit(0);
            }
        } else if (confirmed == 1) {
            dispose();
            openWindows--;
            if (openWindows == 0)
                System.exit(0);
        }
    }

    private int showSaveDialogue() {
        String[] options = {"Save", "Don't save", "Cancel"};
        int confirmed = JOptionPane.showOptionDialog(notepadFrame.this,
                "Save the changes?",
                "Exit...",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );

        return confirmed;
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
                    textArea.setText(readTextFile(path));
                    this.path = path;
                    changesMade = false;
                    setTitle(fileName);
                }
            } else if (confirmed == 1) {
                textArea.setText(readTextFile(path));
                this.path = path;
                changesMade = false;
                setTitle(fileName);
            }
        }


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
            /*
            outerLoop:
            while (checkFilesInDirectory(file, fileName)) {
                String[] options = {"Yes", "Choose a different name", "Cancel"};

                int choice = JOptionPane.showOptionDialog(notepadFrame.this,
                        "Text file already exists. Do you want to continue?",
                        "Text file already exists",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                switch (choice) {
                    case 0:
                        break outerLoop;
                    case 1:
                        fileName = fileNameChooser();

                        if (fileName == null || fileName.equals("/.txt")) {
                            JOptionPane.showMessageDialog(notepadFrame.this,
                                    "You did not enter a name for your file!",
                                    "File Not Saved!",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        break;
                    default:
                        return false;
                }
            }
*/
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
        setTitle(fileName.substring(1, fileName.length() - 4));
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
