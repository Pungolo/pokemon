package ui;



import java.awt.*;

/**
 * Mostra messaggi temporanei centrati sullo schermo.
 */
public class MessageManager {

    private String message;
    private long timestamp;
    private final long DURATION = 2000; // 2 secondi

    public void showMessage(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        if (message == null) return;

        long now = System.currentTimeMillis();
        if (now - timestamp < DURATION) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(10, panelHeight - 50, panelWidth - 20, 40, 15, 15);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(Color.WHITE);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(message);
            g.drawString(message, panelWidth / 2 - textWidth / 2, panelHeight - 25);
        } else {
            message = null;
        }
    }
}
