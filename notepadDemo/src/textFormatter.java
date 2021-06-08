import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

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

    private notepadFrame parent;

    public textFormatter(notepadFrame parent) {
        this.parent = parent;

        setModal(true);
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

    private void makefontJList() {
        fontListPanel = new JPanel();
        fontJList = new JList<>(fonts);
        fontJList.setSelectedIndex(0);
        fontJList.setVisibleRowCount(8);
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontListPanel.add(new JScrollPane(fontJList));
        fontListPanel.setBorder(panelPadding);

//        fontJList.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                System.out.println(fonts[fontJList.getSelectedIndex()]);
//            }
//        });
    }

    private void makestyleJList() {

        String[] styles = {"Regular", "Bold", "Italic", "Bold Italic"};
        stylePanel = new JPanel();
        styleJList = new JList<>(styles);
        styleJList.setSelectedIndex(0);
        styleJList.setVisibleRowCount(8);
        styleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleJList.setBorder(JListPadding);
        stylePanel.add(new JScrollPane(styleJList));
        stylePanel.setBorder(panelPadding);
    }


    private void makeSizeJList() {
        sizePanel = new JPanel();

        sizeJList = new JList<>(sizeArray);
        sizeJList.setSelectedIndex(0);
        sizeJList.setVisibleRowCount(8);
        sizeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeJList.setBorder(JListPadding);
        sizePanel.add(new JScrollPane(sizeJList));
        sizePanel.setBorder(panelPadding);
    }

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
}
