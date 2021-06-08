import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class textFormatter extends JDialog {
    private final String[] fonts =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private final String[] sizeArray = {"8", "9", "10", "11", "12", "14", "16", "18",
            "20", "24", "28", "32", "36", "48", "72"};
    private JList<String> fontJList;
    private JList<String> styleJList;
    private JList<String> sizeJList;
    private JPanel fontListPanel;
    private JPanel stylePanel;
    private JPanel sizePanel;
    private final Border panelPadding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private final Border JListPadding = BorderFactory.createEmptyBorder(0, 0, 0, 20);

    private JButton saveButton;
    private JPanel saveButtonPanel;

    private final String currentFontName;
    private final int currentFontStyle;
    private final int currentFontSize;

    private final notepadFrame parent;

    /**
     * The constructor
     * @param parent the parent window of the dialog
     * @param currentFontName The current font name used in the text area
     * @param currentFontStyle The current font style used in the text area
     * @param currentFontSize The current font size used in the text area
     */
    public textFormatter(notepadFrame parent, String currentFontName,
                         int currentFontStyle, int currentFontSize) {
        this.parent = parent;
        this.currentFontName = currentFontName;
        this.currentFontStyle = currentFontStyle;
        this.currentFontSize = currentFontSize;

        setTitle("Font");
        setLayout(new BorderLayout());

        makefontJList();
        makestyleJList();
        makeSizeJList();
        makeSaveButton();

        add(fontListPanel, BorderLayout.WEST);
        add(stylePanel, BorderLayout.CENTER);
        add(sizePanel, BorderLayout.EAST);
        add(saveButtonPanel, BorderLayout.SOUTH);
        pack();
    }

    /**
     * Makes the font JList
     */
    private void makefontJList() {
        fontListPanel = new JPanel();
        fontJList = new JList<>(fonts);
        fontJList.setVisibleRowCount(8);
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontListPanel.add(new JScrollPane(fontJList));
        fontListPanel.setBorder(panelPadding);

        int currentFontNameIndex = searchCurrentFontName(currentFontName);

        if (currentFontNameIndex == -1) {
            fontJList.setSelectedIndex(0);
        } else {
            fontJList.setSelectedIndex(currentFontNameIndex);
        }
    }

    /**
     * Makes the style JList
     */
    private void makestyleJList() {

        String[] styles = {"Regular", "Bold", "Italic", "Bold Italic"};
        stylePanel = new JPanel();
        styleJList = new JList<>(styles);
        styleJList.setSelectedIndex(currentFontStyle);
        styleJList.setVisibleRowCount(8);
        styleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleJList.setBorder(JListPadding);
        stylePanel.add(new JScrollPane(styleJList));
        stylePanel.setBorder(panelPadding);
    }

    /**
     * Makes the size JList
     */
    private void makeSizeJList() {
        sizePanel = new JPanel();

        sizeJList = new JList<>(sizeArray);
        sizeJList.setSelectedIndex(currentFontSize);
        sizeJList.setVisibleRowCount(8);
        sizeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeJList.setBorder(JListPadding);
        sizePanel.add(new JScrollPane(sizeJList));
        sizePanel.setBorder(panelPadding);

        int currentFontSizeIndex = searchCurrentFontSize(currentFontSize);

        if (currentFontSizeIndex == -1) {
            sizeJList.setSelectedIndex(0);
        } else {
            sizeJList.setSelectedIndex(currentFontSizeIndex);
        }
    }

    /**
     * Makes the save button and adds an action listener to it
     */
    private void makeSaveButton() {
        saveButton = new JButton("SAVE CHANGES");
        saveButtonPanel = new JPanel();

        final Border buttonPadding = BorderFactory.createEmptyBorder(0, 25, 0, 25);
        saveButtonPanel.setBorder(buttonPadding);
        saveButtonPanel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.changeFont(fonts[fontJList.getSelectedIndex()], styleJList.getSelectedIndex(),
                        Integer.parseInt(sizeArray[sizeJList.getSelectedIndex()]));
            }

        });
    }

    /**
     * finds the index of the current font name in the fonts array
     * @param target the current font name
     * @return the index of the current font name in the fonts array
     */
    private int searchCurrentFontName(String target){
        int start = 0;
        int end = fonts.length - 1;

        while(start <= end){
            int middle = (start + end) / 2;

            int result = target.compareTo(fonts[middle]);

            if(result == 0){
                return middle;
            }else if(result > 0){
                start = middle + 1;
            }else{
                end = middle - 1;
            }
        }

        return -1;
    }

    /**
     * finds the index of the current font size in the sizeArray
     * @param target the integer of the current font size
     * @return the index of the current font size in the sizeArray
     */
    private int searchCurrentFontSize(int target){
        int start = 0;
        int end = sizeArray.length - 1;

        while(start <= end){
            int middle = (start + end) / 2;

            if(target == Integer.parseInt(sizeArray[middle])){
                return middle;
            }else if(target > Integer.parseInt(sizeArray[middle])){
                start = middle + 1;
            }else{
                end = middle - 1;
            }
        }

        return -1;
    }
}
