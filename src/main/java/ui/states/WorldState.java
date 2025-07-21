package ui.states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import engine.InputHandler;
import entities.Player;

import main.GamePanel;
import main.GameState;
import ui.states.enums.DirectionKey;
import world.WorldMap;

import java.util.Random;

public class WorldState implements IGameState {

    private static final double ENCOUNTER_PROBABILITY = 0.1; // 10% possibilità di incontro

    private Player player;
    private WorldMap worldMap;
    private Random random;

    public WorldState(Player player, WorldMap worldMap) {
        this.player = player;
        this.worldMap = worldMap;
        this.random = new Random();
    }

    @Override
    public void update(GamePanel panel) {
        long now = System.currentTimeMillis();
        InputHandler input = panel.getInput();

        handleStateTransition(panel, input, now);

        if (handleMovement(input, now, panel)) {
            player.update(); // Aggiorna l'animazione solo se si è mossi
        }
    }

    private void handleStateTransition(GamePanel panel, InputHandler input, long now) {
        if (input.isPressed(KeyEvent.VK_X) &&
                now - panel.getLastMenuToggleTime() > panel.getMenuToggleCooldown()) {
            panel.changeState(GameState.MAIN_MENU);
        }
    }

    private boolean handleMovement(InputHandler input, long now, GamePanel panel) {
        boolean wantsToMove = false;
        int dx = 0, dy = 0;

        for (DirectionKey dirKey : DirectionKey.values()) {
            if (input.isPressed(dirKey.key)) {
                dx = dirKey.dx;
                dy = dirKey.dy;
                if (dirKey.side == null) {
                    player.setDirection(dirKey.dir);
                } else {
                    player.setDirection(dirKey.dir, dirKey.side);
                }
                wantsToMove = true;
                break;
            }
        }


        if (!wantsToMove || !canMove(now)) return false;

        int newX = player.x + dx;
        int newY = player.y + dy;

        if (!worldMap.isWalkable(newX, newY)) return false;

        performMove(dx, dy, now);

        if (shouldTriggerBattle(player.x, player.y)) {
            panel.goToBattle(now);
            return true;
        }

        return true;
    }

    private boolean canMove(long now) {
        return now - player.getLastMoveTime() >= player.getMoveCooldown();
    }

    private void performMove(int dx, int dy, long now) {
        player.move(dx, dy);
        player.setLastMoveTime(now);
    }

    private boolean shouldTriggerBattle(int x, int y) {
        return worldMap.isGrassTile(x, y) && randomEncounter();
    }


    private boolean randomEncounter() {
        return random.nextDouble() < ENCOUNTER_PROBABILITY;
    }

    @Override
    public void draw(GamePanel panel, Graphics2D g) {
        // Rendering gestito altrove (GamePanel)
    }

    @Override
    public void onEnter() {
        System.out.println("Entrando in WorldState...");
        // TODO: Riattivare musica del mondo
        // AudioManager.play("overworld_theme");
    }

    @Override
    public void onExit() {
        System.out.println("Uscendo da WorldState...");
        // TODO: Fermare musica del mondo
        // AudioManager.stop("overworld_theme");
    }
}
