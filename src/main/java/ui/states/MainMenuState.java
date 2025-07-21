package ui.states;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import engine.InputHandler;
import engine.LocalizationManager;
import main.GamePanel;
import main.GameState;

public class MainMenuState implements IGameState {

    private String[] menuOptions;
    private int menuSelection = 0;

    private long lastMenuNavTime = 0;
    private static final long MENU_NAV_COOLDOWN = 150;

    @Override
    public void onEnter() {
        this.menuSelection = 0;
        loadLocalizedTexts();
    }

    private void loadLocalizedTexts() {
        LocalizationManager lm = LocalizationManager.getInstance();
        menuOptions = new String[]{
                lm.getString("menu.pokemon"),
                lm.getString("menu.save"),
                lm.getString("menu.settings"),
                lm.getString("menu.exit")
        };
    }

    @Override
    public void update(GamePanel panel) {
        InputHandler input = panel.getInput();
        long now = System.currentTimeMillis();

        handleExitToWorld(input, panel, now);
        handleNavigation(input, now);
        handleSelection(input, panel);
    }

    private void handleExitToWorld(InputHandler input, GamePanel panel, long now) {
        if (input.isPressed(KeyEvent.VK_X) && canToggleMenu(now, panel)) {
            panel.changeState(GameState.WORLD);
        }
    }

    private boolean canToggleMenu(long now, GamePanel panel) {
        return now - panel.getLastMenuToggleTime() > panel.getMenuToggleCooldown();
    }

    private void handleNavigation(InputHandler input, long now) {
        if (now - lastMenuNavTime < MENU_NAV_COOLDOWN) return;

        if (input.isPressed(KeyEvent.VK_DOWN)) {
            menuSelection = (menuSelection + 1) % menuOptions.length;
            lastMenuNavTime = now;
        } else if (input.isPressed(KeyEvent.VK_UP)) {
            menuSelection = (menuSelection - 1 + menuOptions.length) % menuOptions.length;
            lastMenuNavTime = now;
        }
    }

    private void handleSelection(InputHandler input, GamePanel panel) {
        if (input.isPressed(KeyEvent.VK_Z)) {
            panel.handleMainMenuSelection(menuOptions[menuSelection]);
            input.reset(); // Per evitare input ripetuti
        }
    }

    @Override
    public void draw(GamePanel panel, Graphics2D g) {
        FontMetrics fm = g.getFontMetrics();
        int padding = 10;
        int lineSpacing = 5;
        int lineHeight = fm.getHeight();

        int menuWidth = calculateMenuWidth(fm, padding);
        int menuHeight = calculateMenuHeight(lineHeight, lineSpacing, padding);
        int menuX = panel.getWidth() - menuWidth - 10;
        int menuY = 10;

        drawMenuBackground(g, menuX, menuY, menuWidth, menuHeight);
        drawMenuOptions(g, fm, menuX, menuY, lineHeight, lineSpacing, padding);
    }

    private int calculateMenuWidth(FontMetrics fm, int padding) {
        int maxWidth = 0;
        for (String option : menuOptions) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(option));
        }
        return maxWidth + padding * 2;
    }

    private int calculateMenuHeight(int lineHeight, int spacing, int padding) {
        return (lineHeight * menuOptions.length) + (spacing * (menuOptions.length - 1)) + (padding * 2);
    }

    private void drawMenuBackground(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }

    private void drawMenuOptions(Graphics2D g, FontMetrics fm, int x, int y, int lineHeight, int spacing, int padding) {
        for (int i = 0; i < menuOptions.length; i++) {
            g.setColor(i == menuSelection ? Color.YELLOW : Color.WHITE);
            int textY = y + padding + fm.getAscent() + (i * (lineHeight + spacing));
            g.drawString(menuOptions[i], x + padding, textY);
        }
    }

    @Override
    public void onExit() {
        // Potresti voler salvare lo stato del menu o fermare suoni
    }
}
