package editor;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FindReplaceDialog extends JDialog {
    private final JTextArea textArea;
    private final AtomicInteger lastIndex;
    private JTextField findField;
    private JTextField replaceField;
    private JCheckBox matchCase;
    private JCheckBox wholeWord;

    public FindReplaceDialog(JFrame parent, JTextArea textArea) {
        super(parent, "Find/Replace", false);
        this.textArea = textArea;
        this.lastIndex = new AtomicInteger(-1);
        
        initializeComponents();
        setupLayout();
        setupActions();
        
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        findField = new JTextField(30);
        replaceField = new JTextField(30);
        matchCase = new JCheckBox("Match case");
        wholeWord = new JCheckBox("Whole word");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(5, 5));
        setResizable(false);

        JPanel findPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel replacePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        findPanel.add(new JLabel("Find:"));
        findPanel.add(findField);

        replacePanel.add(new JLabel("Replace with:"));
        replacePanel.add(replaceField);

        optionsPanel.add(matchCase);
        optionsPanel.add(wholeWord);

        JButton findNextBtn = new JButton("Find Next");
        JButton replaceBtn = new JButton("Replace");
        JButton replaceAllBtn = new JButton("Replace All");
        JButton closeBtn = new JButton("Close");

        buttonsPanel.add(findNextBtn);
        buttonsPanel.add(replaceBtn);
        buttonsPanel.add(replaceAllBtn);
        buttonsPanel.add(closeBtn);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerPanel.add(findPanel);
        centerPanel.add(replacePanel);
        centerPanel.add(optionsPanel);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        findNextBtn.addActionListener(e -> findNext());
        replaceBtn.addActionListener(e -> replace());
        replaceAllBtn.addActionListener(e -> replaceAll());
        closeBtn.addActionListener(e -> dispose());
    }

    private void setupActions() {
        getRootPane().setDefaultButton(findField.getDocument().getLength() == 0 ? 
            (JButton)((JPanel)getContentPane().getComponent(1)).getComponent(0) : // Find Next button
            (JButton)((JPanel)getContentPane().getComponent(1)).getComponent(1)); // Replace button
    }

    private void findNext() {
        String searchText = findField.getText();
        String content = textArea.getText();
        
        if (searchText.isEmpty()) return;

        if (!matchCase.isSelected()) {
            searchText = searchText.toLowerCase();
            content = content.toLowerCase();
        }

        int startIndex = Math.max(lastIndex.get() + 1, textArea.getCaretPosition());
        if (startIndex >= content.length() || startIndex < 0) {
            startIndex = 0;
        }

        int foundIndex;
        if (wholeWord.isSelected()) {
            foundIndex = findWholeWord(content, searchText, startIndex);
        } else {
            foundIndex = content.indexOf(searchText, startIndex);
        }

        if (foundIndex != -1) {
            textArea.setCaretPosition(foundIndex);
            textArea.select(foundIndex, foundIndex + findField.getText().length());
            textArea.requestFocusInWindow();
            lastIndex.set(foundIndex);
        } else {
            lastIndex.set(-1);
            JOptionPane.showMessageDialog(this,
                    "Cannot find \"" + findField.getText() + "\"",
                    "Notepad Demo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void replace() {
        if (textArea.getSelectedText() != null) {
            textArea.replaceSelection(replaceField.getText());
            findNext();
        } else {
            findNext();
        }
    }

    private void replaceAll() {
        String searchText = findField.getText();
        String replaceText = replaceField.getText();
        String content = textArea.getText();
        
        if (searchText.isEmpty()) return;

        int replacements = 0;
        StringBuilder newContent = new StringBuilder(content);
        
        lastIndex.set(-1);
        
        while (true) {
            String searchIn = newContent.toString();
            if (!matchCase.isSelected()) {
                searchIn = searchIn.toLowerCase();
                searchText = searchText.toLowerCase();
            }

            int foundIndex;
            if (wholeWord.isSelected()) {
                foundIndex = findWholeWord(searchIn, searchText, lastIndex.get() + 1);
            } else {
                foundIndex = searchIn.indexOf(searchText, lastIndex.get() + 1);
            }

            if (foundIndex == -1) break;

            newContent.replace(foundIndex, foundIndex + searchText.length(), replaceText);
            lastIndex.set(foundIndex + replaceText.length() - 1);
            replacements++;
        }

        if (replacements > 0) {
            textArea.setText(newContent.toString());
            JOptionPane.showMessageDialog(this,
                    "Replaced " + replacements + " occurrence(s)",
                    "Notepad Demo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int findWholeWord(String content, String word, int startIndex) {
        while (true) {
            int index = content.indexOf(word, startIndex);
            if (index == -1) return -1;

            boolean startValid = index == 0 || !Character.isLetterOrDigit(content.charAt(index - 1));
            boolean endValid = index + word.length() >= content.length() || 
                             !Character.isLetterOrDigit(content.charAt(index + word.length()));

            if (startValid && endValid) {
                return index;
            }
            startIndex = index + 1;
        }
    }
}
