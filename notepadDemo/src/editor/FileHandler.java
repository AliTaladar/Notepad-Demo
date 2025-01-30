package editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FileHandler {
    private final JTextArea textArea;
    private final JFrame parentFrame;
    private String currentPath;
    private boolean autoSaveEnabled;
    private final JLabel statusBar;

    public FileHandler(JFrame parentFrame, JTextArea textArea, JLabel statusBar) {
        this.parentFrame = parentFrame;
        this.textArea = textArea;
        this.statusBar = statusBar;
        this.currentPath = null;
        this.autoSaveEnabled = false;
    }

    public boolean saveFile() {
        if (currentPath == null) {
            return saveFileAs();
        }
        boolean saved = writeToFile(currentPath);
        if (saved) {
            updateFrameTitle(currentPath);
        }
        return saved;
    }

    public boolean saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:/Users/"));
        fileChooser.setDialogTitle("Save as...");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        if (fileChooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".txt")) {
                path += ".txt";
            }
            currentPath = path;
            boolean saved = writeToFile(path);
            if (saved) {
                updateFrameTitle(path);
            }
            return saved;
        }
        return false;
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("C:/Users/"));
        fileChooser.setDialogTitle("Select a file...");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        if (fileChooser.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                // Read the file content into a string
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                
                // Set the path before changing text to ensure auto-save works
                currentPath = fileChooser.getSelectedFile().getAbsolutePath();
                
                // Use setText which will trigger the document listener
                textArea.setText(content.toString());
                
                updateFrameTitle(currentPath);
                updateStatusBar("File opened successfully");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Error reading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean writeToFile(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            String text = textArea.getText();
            writer.write(text);
            updateStatusBar("File saved successfully");
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void autoSave() {
        if (currentPath != null && autoSaveEnabled) {
            if (writeToFile(currentPath)) {
                updateStatusBar("Auto-saved");
                // Remove the "*" from the title since we've saved
                String currentTitle = parentFrame.getTitle();
                if (currentTitle.endsWith("*")) {
                    SwingUtilities.invokeLater(() -> {
                        parentFrame.setTitle(currentTitle.substring(0, currentTitle.length() - 1));
                    });
                }
            } else {
                autoSaveEnabled = false;
                updateStatusBar("Auto-save failed");
            }
        }
    }

    public void setAutoSaveEnabled(boolean enabled) {
        if (enabled && currentPath == null) {
            // If enabling auto-save but no file exists, we need to save first
            if (!saveFileAs()) {
                enabled = false;
                updateStatusBar("Auto-save cancelled - No save location");
                return;
            }
        }
        this.autoSaveEnabled = enabled;
        
        // If enabling auto-save, do an immediate save
        if (enabled) {
            autoSave();
        }
        
        updateStatusBar(enabled ? "Auto-save enabled" : "Auto-save disabled");
    }

    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    private void updateFrameTitle(String path) {
        String fileName = Paths.get(path).getFileName().toString();
        parentFrame.setTitle(fileName);
    }

    public String getCurrentPath() {
        return currentPath;
    }

    private void updateStatusBar(String message) {
        if (statusBar != null) {
            String currentStatus = statusBar.getText();
            statusBar.setText(message);
            Timer timer = new Timer(3000, e -> statusBar.setText(currentStatus));
            timer.setRepeats(false);
            timer.start();
        }
    }
}
