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
        trace("openFile called");
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
                trace("openFile successful - path: " + currentPath);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Error reading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                trace("openFile failed with error: " + ex.getMessage());
            }
        } else {
            trace("openFile cancelled by user");
        }
    }

    private boolean writeToFile(String path) {
        trace("writeToFile called with path: " + path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            String text = textArea.getText();
            writer.write(text);
            trace("writeToFile successful - wrote " + text.length() + " characters");
            updateStatusBar("File saved successfully");
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Error saving file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            trace("writeToFile failed with error: " + ex.getMessage());
            return false;
        }
    }

    public void autoSave() {
        trace("autoSave called - currentPath: " + currentPath + ", autoSaveEnabled: " + autoSaveEnabled);
        if (currentPath != null && autoSaveEnabled) {
            trace("autoSave - saving to: " + currentPath);
            if (writeToFile(currentPath)) {
                updateStatusBar("Auto-saved");
                // Remove the "*" from the title since we've saved
                String currentTitle = parentFrame.getTitle();
                if (currentTitle.endsWith("*")) {
                    SwingUtilities.invokeLater(() -> {
                        parentFrame.setTitle(currentTitle.substring(0, currentTitle.length() - 1));
                    });
                }
                trace("autoSave successful");
            } else {
                autoSaveEnabled = false;
                updateStatusBar("Auto-save failed");
                trace("autoSave failed - disabled auto-save");
            }
        } else {
            trace("autoSave - conditions not met: currentPath=" + currentPath + ", autoSaveEnabled=" + autoSaveEnabled);
        }
    }

    public void setAutoSaveEnabled(boolean enabled) {
        trace("setAutoSaveEnabled called with: " + enabled + " - currentPath: " + currentPath);
        if (enabled && currentPath == null) {
            // If enabling auto-save but no file exists, we need to save first
            if (!saveFileAs()) {
                enabled = false;
                updateStatusBar("Auto-save cancelled - No save location");
                trace("setAutoSaveEnabled failed - no save location selected");
                return;
            }
        }
        this.autoSaveEnabled = enabled;
        
        // If enabling auto-save, do an immediate save
        if (enabled) {
            trace("setAutoSaveEnabled - doing immediate save");
            autoSave();
        }
        
        updateStatusBar(enabled ? "Auto-save enabled" : "Auto-save disabled");
        trace("setAutoSaveEnabled complete - autoSaveEnabled: " + autoSaveEnabled);
    }

    public boolean isAutoSaveEnabled() {
        trace("isAutoSaveEnabled called - returning: " + autoSaveEnabled);
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

    private void trace(String message) {
        try (FileWriter fw = new FileWriter("notepadDemo/trace.txt", true)) {
            String timestamp = LocalDateTime.now().toString();
            fw.write(timestamp + " - [FileHandler] " + message + "\n");
        } catch (IOException e) {
            // Ignore trace errors
        }
    }
}
