package ui.states;

import java.awt.*;
import java.awt.event.KeyEvent;

import engine.InputHandler;
import engine.LocalizationManager;
import main.GamePanel;
import main.GameState;


public class SettingsMenuState implements IGameState {

    private final String[] optionsKeys = {
            "settings.language",
            "settings.volume",
            "settings.controls",
            "settings.back"
    };

    private int selectionIndex = 0;
    private long lastNavTime = 0;
    private static final long NAV_COOLDOWN = 150;

    @Override
    public void onEnter() {
        selectionIndex = 0;
        System.out.println("Entrando in SettingsMenuState...");
    }

    @Override
    public void update(GamePanel panel) {
        InputHandler input = panel.getInput();
        long now = System.currentTimeMillis();

        if (handleExit(input, panel, now)) return;
        handleNavigation(input, now);
        handleSelection(input, panel);
    }

    private boolean handleExit(InputHandler input, GamePanel panel, long now) {
        if ((input.isPressed(KeyEvent.VK_X) || input.isPressed(KeyEvent.VK_ESCAPE))
                && now - panel.getLastMenuToggleTime() > panel.getMenuToggleCooldown()) {
            panel.changeState(GameState.WORLD);
            return true;
        }
        return false;
    }

    private void handleNavigation(InputHandler input, long now) {
        if (now - lastNavTime < NAV_COOLDOWN) return;

        if (input.isPressed(KeyEvent.VK_DOWN)) {
            selectionIndex = (selectionIndex + 1) % optionsKeys.length;
            lastNavTime = now;
        } else if (input.isPressed(KeyEvent.VK_UP)) {
            selectionIndex = (selectionIndex - 1 + optionsKeys.length) % optionsKeys.length;
            lastNavTime = now;
        }
    }

    private void handleSelection(InputHandler input, GamePanel panel) {
        if (input.isPressed(KeyEvent.VK_Z)) {
            String selectedKey = optionsKeys[selectionIndex];
            switch (selectedKey) {
                case "settings.language":
                    // TODO: implementa cambio lingua
                    System.out.println("Lingua selezionata.");
                    break;
                case "settings.volume":
                    // TODO: implementa regolazione volume
                    System.out.println("Volume selezionato.");
                    break;
                case "settings.controls":
                    // TODO: implementa configurazione controlli
                    System.out.println("Controlli selezionati.");
                    break;
                case "settings.back":
                    panel.changeState(GameState.WORLD);
                    break;
            }
            input.reset(); // Previene doppia pressione
        }
    }

    @Override
    public void draw(GamePanel panel, Graphics2D g) {
        drawBackground(panel, g);
        drawOptions(panel, g);
    }

    private void drawBackground(GamePanel panel, Graphics2D g) {
        float borderPercentage = 0.10f;
        int borderX = (int) (panel.getWidth() * borderPercentage);
        int borderY = (int) (panel.getHeight() * borderPercentage);
        int width = panel.getWidth() - borderX * 2;
        int height = panel.getHeight() - borderY * 2;

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(borderX, borderY, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(borderX, borderY, width, height);

        String title = LocalizationManager.getInstance().getString("menu.settings");
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, panel.getWidth() / 2 - fm.stringWidth(title) / 2, borderY + fm.getAscent() + 20);
    }

    private void drawOptions(GamePanel panel, Graphics2D g) {
        LocalizationManager lm = LocalizationManager.getInstance();
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();

        int startY = 150;
        int spacing = 40;

        for (int i = 0; i < optionsKeys.length; i++) {
            String optionText = lm.getString(optionsKeys[i]);

            if (i == selectionIndex) {
                g.setColor(new Color(255, 255, 0, 100)); // Evidenzia opzione selezionata
                g.fillRect(panel.getWidth() / 2 - 160, startY + i * spacing - 20, 320, 30);
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            int textX = panel.getWidth() / 2 - fm.stringWidth(optionText) / 2;
            int textY = startY + i * spacing;
            g.drawString(optionText, textX, textY);
        }
    }

    @Override
    public void onExit() {
        System.out.println("Uscendo da SettingsMenuState...");
    }
}
