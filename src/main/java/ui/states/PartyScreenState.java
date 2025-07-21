package ui.states;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import engine.InputHandler;
import engine.LocalizationManager;
import entities.Player;
import entities.Pokemon;

import main.GamePanel;
import main.GameState;


public class PartyScreenState implements IGameState {

    private final Player player;
    private int selectedIndex = 0;
    private int swapIndex = -1; // -1 = nessuno in scambio

    private long lastNavTime = 0;
    private static final long NAV_COOLDOWN = 150;

    public PartyScreenState(Player player) {
        this.player = player;
    }

    @Override
    public void onEnter() {
        selectedIndex = 0;
        swapIndex = -1;
        System.out.println("Entrando in PartyScreenState...");
    }

    @Override
    public void update(GamePanel panel) {
        InputHandler input = panel.getInput();
        long now = System.currentTimeMillis();

        if (handleExit(input, panel, now)) return;

        handleNavigation(input, now);
        handleSelection(input);
    }

    private boolean handleExit(InputHandler input, GamePanel panel, long now) {
        if (input.isPressed(KeyEvent.VK_X) || input.isPressed(KeyEvent.VK_ESCAPE)) {
            if (swapIndex != -1) {
                swapIndex = -1; // Annulla scambio
                input.reset();
            } else if (now - panel.getLastMenuToggleTime() > panel.getMenuToggleCooldown()) {
                panel.changeState(GameState.WORLD);
            }
            return true;
        }
        return false;
    }

    private void handleNavigation(InputHandler input, long now) {
        if (now - lastNavTime < NAV_COOLDOWN) return;

        int partySize = player.getParty().getSize();
        if (input.isPressed(KeyEvent.VK_DOWN) && selectedIndex < partySize - 1) {
            selectedIndex++;
            lastNavTime = now;
        } else if (input.isPressed(KeyEvent.VK_UP) && selectedIndex > 0) {
            selectedIndex--;
            lastNavTime = now;
        }
    }

    private void handleSelection(InputHandler input) {
        if (input.isPressed(KeyEvent.VK_Z)) {
            if (swapIndex == -1) {
                swapIndex = selectedIndex; // Inizia lo scambio
            } else {
                player.getParty().swap(swapIndex, selectedIndex); // Completa lo scambio
                swapIndex = -1;
            }
            input.reset(); // Evita input ripetuti
        }
    }

    @Override
    public void draw(GamePanel panel, Graphics2D g) {
        drawMenuBackground(panel, g);

        List<Pokemon> party = player.getParty().getPokemonList();
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < party.size(); i++) {
            drawPokemonEntry(g, panel, party.get(i), i, fm);
        }

        drawControlsHint(panel, g);
    }

    private void drawPokemonEntry(Graphics2D g, GamePanel panel, Pokemon p, int index, FontMetrics fm) {
        int y = 100 + index * 50;
        int x = panel.getWidth() / 2 - 160;
        int width = 320;
        int height = 30;

        if (swapIndex == index) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRect(x, y - 22, width, height);
        } else if (index == selectedIndex) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(x, y - 22, width, height);
        }

        g.setColor(Color.WHITE);
        String text = String.format("%s  %s%d  %s %d/%d",
                p.getName(),
                LocalizationManager.getInstance().getString("party.levelAbbr"), p.getLevel(),
                LocalizationManager.getInstance().getString("party.hpLabel"), p.getCurrentHp(), p.getMaxHp()
        );

        g.drawString(text, panel.getWidth() / 2 - fm.stringWidth(text) / 2, y);
    }

    private void drawControlsHint(GamePanel panel, Graphics2D g) {
        String controls = LocalizationManager.getInstance().getString("party.controls");
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.GRAY);
        g.drawString(controls, panel.getWidth() / 2 - fm.stringWidth(controls) / 2, panel.getHeight() - 40);
    }

    private void drawMenuBackground(GamePanel panel, Graphics2D g) {
        float borderPercentage = 0.10f;
        int borderX = (int) (panel.getWidth() * borderPercentage);
        int borderY = (int) (panel.getHeight() * borderPercentage);
        int menuWidth = panel.getWidth() - (borderX * 2);
        int menuHeight = panel.getHeight() - (borderY * 2);

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(borderX, borderY, menuWidth, menuHeight);
        g.setColor(Color.WHITE);
        g.drawRect(borderX, borderY, menuWidth, menuHeight);

        String title = LocalizationManager.getInstance().getString("party.title");
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, panel.getWidth() / 2 - fm.stringWidth(title) / 2, borderY + fm.getAscent() + 20);
    }

    @Override
    public void onExit() {
        System.out.println("Uscendo da PartyScreenState...");
    }
}
