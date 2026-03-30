import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Custom File Uploader Button Component for NetBeans Palette
 * Looks like a simple rounded blue "Upload File" button
 */
public class FileUploader extends JPanel {

    private File selectedFile;
    private String[] allowedExtensions = null;
    private long maxFileSizeBytes = -1;

    private Color buttonColor = new Color(91, 155, 213);
    private Color hoverColor = new Color(70, 130, 190);
    private Color pressColor = new Color(50, 110, 170);
    private Color currentColor;

    private String buttonText = "Upload File";
    private boolean isHovered = false;

    public FileUploader() {
        initComponent();
    }

    private void initComponent() {
        setPreferredSize(new Dimension(260, 70));
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        currentColor = buttonColor;

        // Hover and click effects
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                currentColor = hoverColor;
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                currentColor = buttonColor;
                repaint();
            }
            public void mousePressed(MouseEvent e) {
                currentColor = pressColor;
                repaint();
            }
            public void mouseReleased(MouseEvent e) {
                currentColor = isHovered ? hoverColor : buttonColor;
                repaint();
            }
            public void mouseClicked(MouseEvent e) {
                openFileChooser();
            }
        });

        // Drag and Drop
        setDropTarget(new DropTarget(this, new DropTargetListener() {
            public void dragEnter(DropTargetDragEvent e) {
                if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    currentColor = pressColor;
                    repaint();
                    e.acceptDrag(DnDConstants.ACTION_COPY);
                }
            }
            public void dragOver(DropTargetDragEvent e) {}
            public void dropActionChanged(DropTargetDragEvent e) {}
            public void dragExit(DropTargetEvent e) {
                currentColor = buttonColor;
                repaint();
            }
            public void drop(DropTargetDropEvent e) {
                try {
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) e.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) handleFile(files.get(0));
                    e.dropComplete(true);
                } catch (Exception ex) {
                    e.dropComplete(false);
                }
                currentColor = buttonColor;
                repaint();
            }
        }));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Smooth rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 35; // rounded corners

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(3, 5, w - 6, h - 6, arc, arc);

        // Draw button background
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, w - 3, h - 5, arc, arc);

        // Draw text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();

        String text = selectedFile != null
            ? shortenFileName(selectedFile.getName(), 22)
            : buttonText;

        int textX = (w - fm.stringWidth(text)) / 2;
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent() - 2;
        g2.drawString(text, textX, textY);

        g2.dispose();
    }

    private void openFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a File");

        if (allowedExtensions != null && allowedExtensions.length > 0) {
            String desc = "Allowed (*." + String.join(", *.", allowedExtensions) + ")";
            chooser.setFileFilter(new FileNameExtensionFilter(desc, allowedExtensions));
            chooser.setAcceptAllFileFilterUsed(false);
        }

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            handleFile(chooser.getSelectedFile());
        }
    }

    private void handleFile(File file) {
        // Check extension
        if (allowedExtensions != null) {
            String name = file.getName().toLowerCase();
            boolean allowed = false;
            for (String ext : allowedExtensions) {
                if (name.endsWith("." + ext.toLowerCase())) { allowed = true; break; }
            }
            if (!allowed) {
                JOptionPane.showMessageDialog(this,
                    "File type not allowed!\nAllowed: " + String.join(", ", allowedExtensions),
                    "Invalid File Type", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Check size
        if (maxFileSizeBytes > 0 && file.length() > maxFileSizeBytes) {
            JOptionPane.showMessageDialog(this,
                "File too large! Max: " + formatFileSize(maxFileSizeBytes),
                "File Too Large", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedFile = file;
        buttonColor = new Color(60, 179, 113); // turns green when file selected ✅
        hoverColor  = new Color(46, 160, 95);
        pressColor  = new Color(34, 139, 70);
        currentColor = buttonColor;
        repaint();
    }

    private String shortenFileName(String name, int maxChars) {
        if (name.length() <= maxChars) return name;
        return name.substring(0, maxChars - 3) + "...";
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    // ===== PUBLIC METHODS =====

    /** Get the selected File */
    public File getFile() { return selectedFile; }

    /** Get full path */
    public String getFilePath() {
        return selectedFile != null ? selectedFile.getAbsolutePath() : "";
    }

    /** Get file name only */
    public String getFileName() {
        return selectedFile != null ? selectedFile.getName() : "";
    }

    /** Check if a file is selected */
    public boolean hasFile() { return selectedFile != null; }

    /** Clear the selected file */
    public void clearFile() {
        selectedFile = null;
        buttonColor  = new Color(91, 155, 213);
        hoverColor   = new Color(70, 130, 190);
        pressColor   = new Color(50, 110, 170);
        currentColor = buttonColor;
        repaint();
    }

    /** Change button label */
    public void setButtonText(String text) {
        this.buttonText = text;
        repaint();
    }

    /** Restrict file types e.g. {"jpg","png","pdf"} */
    public void setAllowedExtensions(String[] extensions) {
        this.allowedExtensions = extensions;
    }

    /** Set max file size in bytes */
    public void setMaxFileSize(long bytes) {
        this.maxFileSizeBytes = bytes;
    }

    /** Change button color */
    public void setButtonColor(Color color) {
        this.buttonColor = color;
        this.currentColor = color;
        repaint();
    }
}