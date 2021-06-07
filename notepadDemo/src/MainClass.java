import javax.swing.*;

public class MainClass {
    public static void main(String[] args) {
        System.gc();
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {

        }

        ImageIcon imageIcon = new ImageIcon("Notepad_Logo.png");

        notepadFrame frame = new notepadFrame("");
//        frame.setIconImage(imageIcon.getImage());
        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);

    }
}
