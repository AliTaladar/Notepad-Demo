package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.*;

public class PrintManager {
    private final JFrame parentFrame;
    private final JTextArea textArea;
    private final JLabel statusBar;
    private PrinterJob printerJob;
    private PageFormat pageFormat;

    public PrintManager(JFrame parentFrame, JTextArea textArea, JLabel statusBar) {
        this.parentFrame = parentFrame;
        this.textArea = textArea;
        this.statusBar = statusBar;
        initializePrinter();
    }

    private void initializePrinter() {
        printerJob = PrinterJob.getPrinterJob();
        pageFormat = printerJob.defaultPage();
    }

    public void showPageSetup() {
        pageFormat = printerJob.pageDialog(pageFormat);
    }

    public void showPrintPreview() {
        JDialog previewDialog = new JDialog(parentFrame, "Print Preview", true);
        previewDialog.setLayout(new BorderLayout());

        PreviewPanel previewPanel = new PreviewPanel();
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        
        JPanel buttonPanel = new JPanel();
        JButton printButton = new JButton("Print");
        JButton closeButton = new JButton("Close");
        
        printButton.addActionListener(e -> {
            previewDialog.dispose();
            print();
        });
        closeButton.addActionListener(e -> previewDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        previewDialog.add(scrollPane, BorderLayout.CENTER);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        previewDialog.setSize(600, 800);
        previewDialog.setLocationRelativeTo(parentFrame);
        previewDialog.setVisible(true);
    }

    public void print() {
        printerJob.setPrintable((graphics, pf, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            float scale = (float) (pf.getImageableWidth() / textArea.getWidth());
            g2d.scale(scale, scale);

            textArea.paint(g2d);
            return Printable.PAGE_EXISTS;
        }, pageFormat);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                updateStatusBar("Document printed successfully");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parentFrame,
                    "Error printing document: " + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private class PreviewPanel extends JPanel implements Printable {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            double scale = Math.min(
                getWidth() / pageFormat.getWidth(),
                getHeight() / pageFormat.getHeight()
            ) * 0.9;
            
            g2d.translate(
                (getWidth() - pageFormat.getWidth() * scale) / 2,
                (getHeight() - pageFormat.getHeight() * scale) / 2
            );
            g2d.scale(scale, scale);
            
            g2d.setColor(Color.WHITE);
            g2d.fill(new Rectangle2D.Double(0, 0, pageFormat.getWidth(), pageFormat.getHeight()));
            g2d.setColor(Color.BLACK);
            g2d.draw(new Rectangle2D.Double(0, 0, pageFormat.getWidth(), pageFormat.getHeight()));
            
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            float contentScale = (float) (pageFormat.getImageableWidth() / textArea.getWidth());
            g2d.scale(contentScale, contentScale);
            textArea.paint(g2d);
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            float scale = (float) (pageFormat.getImageableWidth() / textArea.getWidth());
            g2d.scale(scale, scale);
            
            textArea.paint(g2d);
            return Printable.PAGE_EXISTS;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 800);
        }
    }
}
