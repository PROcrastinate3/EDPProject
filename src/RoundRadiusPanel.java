import javax.swing.*;
import java.awt.*;

public class RoundRadiusPanel extends JPanel {

    private int cornerRadius = 20;

    public RoundRadiusPanel() {   // REQUIRED for NetBeans
        setOpaque(false);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
    }
}