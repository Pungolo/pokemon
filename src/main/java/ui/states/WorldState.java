package ui.states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import engine.InputHandler;
import entities.Player;
import lombok.extern.slf4j.Slf4j;
import main.GamePanel;
import main.GameState;
import ui.states.enums.DirectionKey;
import world.WorldMap;

import java.util.Random;

@Slf4j
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

        if (wantsToMove && now - player.getLastMoveTime() >= player.getMoveCooldown()) {
            int newX = player.x + dx;
            int newY = player.y + dy;

            if (worldMap.isWalkable(newX, newY)) {
                player.move(dx, dy);
                player.setLastMoveTime(now);

                if (worldMap.isGrassTile(player.x, player.y)) {
                    if (randomEncounter()) {
                        panel.goToBattle(now); // Cambia stato
                        return true;
                    }
                }

                return true;
            }
        }

        return false;
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
        log.info("Entrando in WorldState...");
        // TODO: Riattivare musica del mondo
        // AudioManager.play("overworld_theme");
    }

    @Override
    public void onExit() {
        log.info("Uscendo da WorldState...");
        // TODO: Fermare musica del mondo
        // AudioManager.stop("overworld_theme");
    }
}
