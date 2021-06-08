import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

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

    public textFormatter(){

        setModal(true);
        setTitle("Font");
        setLayout(new BorderLayout());

        makefontJList();
        makestyleJList();
        makeSizeJList();

        add(fontListPanel, BorderLayout.WEST);
        add(stylePanel, BorderLayout.CENTER);
        add(sizePanel, BorderLayout.EAST);
        pack();
    }

    private JPanel makefontJList(){
        fontListPanel = new JPanel();
        fontJList = new JList<>(fonts);
        fontJList.setVisibleRowCount(8);
        fontJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontListPanel.add(new JScrollPane(fontJList));
        fontListPanel.setBorder(panelPadding);

        return fontListPanel;
    }

    private JPanel makestyleJList(){

        String[] styles = {"Regular", "Italic", "Bold", "Bold Italic"};
        stylePanel = new JPanel();
        styleJList = new JList<>(styles);
        styleJList.setVisibleRowCount(8);
        styleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleJList.setBorder(JListPadding);
        stylePanel.add(new JScrollPane(styleJList));
        stylePanel.setBorder(panelPadding);

        return stylePanel;
    }


    private JPanel makeSizeJList(){
        sizePanel = new JPanel();

        sizeJList = new JList<>(sizeArray);
        sizeJList.setVisibleRowCount(8);
        sizeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sizeJList.setBorder(JListPadding);
        sizePanel.add(new JScrollPane(sizeJList));
        sizePanel.setBorder(panelPadding);

        return sizePanel;
    }
}
