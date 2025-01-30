package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextEditor {
    private JTextArea textArea;
    private JLabel statusBar;

    public TextEditor(JTextArea textArea, JLabel statusBar) {
        this.textArea = textArea;
        this.statusBar = statusBar;
    }

    public void setupTextArea() {
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    }

    public void updateStatistics() {
        String text = textArea.getText();
        int chars = text.length();
        int charsNoSpaces = text.replaceAll("\\s+", "").length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        int lines = text.isEmpty() ? 1 : text.split("\n").length;
        
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

    public void cleanup() {
        // Nothing to clean up anymore
    }

    public JTextArea getTextArea() {
        return textArea;
    }
}
