package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

import battle.BattleHandler;
import engine.InputHandler;
import engine.LocalizationManager;
import engine.models.ChoiceContext;
import entities.Player;
import entities.Pokemon;
import render.WorldRenderer;
import ui.MessageManager;
import ui.states.*;
import world.WorldMap;


public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    public static final int TILE_SIZE = 32;
    public static final int SCALE = 2;
    public static final int WIDTH = TILE_SIZE * 16;
    public static final int HEIGHT = TILE_SIZE * 12;
    public static final int FPS = 60;

    private Player player;
    private WorldMap worldMap;
    private InputHandler input;
    private Thread gameThread;
    private GameWindow window;

    private Map<GameState, IGameState> gameStates;
    private IGameState currentState;

    private long lastMenuToggleTime = 0;
    private final long menuToogleCooldown = 200;

    private boolean inBattle = false;

    private BufferedImage tileSet;
    private BufferedImage[][] tiles;
    private int tileCols, tileRows;

    private final MessageManager messageManager = new MessageManager();
    private final WorldRenderer worldRenderer = new WorldRenderer();

    public GamePanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setBackground(Color.BLACK);
        setFocusable(true);

        input = new InputHandler();
        addKeyListener(input);

        worldMap = new WorldMap(30, 20);
        player = new Player(4, 6, TILE_SIZE);
        player.getParty().loadFromFile();

        loadGraphics();
        initializeStates();
        startGameLoop();
    }

    private void loadGraphics() {
        tileSet = utils.SpriteLoader.load("../assets/sprites/tileset.png");
        tileCols = tileSet.getWidth() / TILE_SIZE;
        tileRows = tileSet.getHeight() / TILE_SIZE;
        tiles = new BufferedImage[tileRows][tileCols];

        for (int y = 0; y < tileRows; y++) {
            for (int x = 0; x < tileCols; x++) {
                tiles[y][x] = tileSet.getSubimage(
                        x * TILE_SIZE, y * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE
                );
            }
        }

        worldRenderer.init(tiles, TILE_SIZE, SCALE, WIDTH, HEIGHT);
    }

    private void initializeStates() {
        gameStates = new EnumMap<>(GameState.class);
        gameStates.put(GameState.WORLD, new WorldState(player, worldMap));
        gameStates.put(GameState.MAIN_MENU, new MainMenuState());
        gameStates.put(GameState.SETTINGS_MENU, new SettingsMenuState());
        gameStates.put(GameState.PARTY_SCREEN, new PartyScreenState(player));
        gameStates.put(GameState.CHOICE_BOX, new ChoiceState());

        changeState(GameState.WORLD);
    }

    public void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (!inBattle && currentState != null) {
            currentState.update(this);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    private void draw(Graphics2D g) {
        worldRenderer.drawWorld(g, worldMap, player);
        if (currentState != null) currentState.draw(this, g);
        messageManager.draw(g, getWidth(), getHeight());
    }

    public void goToBattle(long now) {
        player.setLastMoveTime(now);
        if (!worldMap.isGrassTile(player.x, player.y)) return;

        if (Math.random() < 0.2) {
            inBattle = true;
            Pokemon wild = new Pokemon("Bulbasaur", 5, 10, 10, 10, 15);
            Pokemon playerMon = player.getParty().getPokemon(0);
            BattleHandler handler = new BattleHandler(window, this, player);
            handler.startBattle(playerMon, wild);
        }
    }

    public void changeState(GameState newStateKey) {
        if (currentState != null) currentState.onExit();
        currentState = gameStates.get(newStateKey);
        if (currentState != null) {
            lastMenuToggleTime = System.currentTimeMillis();
            currentState.onEnter();
        }
    }

    public void handleMainMenuSelection(String selectedOptionKey) {
        LocalizationManager lm = LocalizationManager.getInstance();

        switch (selectedOptionKey) {
            case "menu.pokemon" -> changeState(GameState.PARTY_SCREEN);
            case "menu.save" -> {
                if (player.getParty().saveToFile()) {
                    messageManager.showMessage(lm.getString("save.success"));
                }
                changeState(GameState.WORLD);
            }
            case "menu.settings" -> changeState(GameState.SETTINGS_MENU);
            case "menu.exit" -> askToExitGame();
        }
    }

    private void askToExitGame() {
        LocalizationManager lm = LocalizationManager.getInstance();

        Runnable saveAndExit = () -> {
            player.getParty().saveToFile();
            System.exit(0);
        };

        Runnable confirmExitNoSave = () -> {
            ChoiceContext confirm = new ChoiceContext(
                    lm.getString("exit.confirm.nosave"),
                    List.of(lm.getString("choice.yes"), lm.getString("choice.no")),
                    List.of(() -> System.exit(0), () -> changeState(GameState.WORLD)),
                    () -> changeState(GameState.WORLD)
            );
            ((ChoiceState) gameStates.get(GameState.CHOICE_BOX)).configure(confirm);
        };

        ChoiceContext context = new ChoiceContext(
                lm.getString("exit.confirm.save"),
                List.of(lm.getString("choice.yes"), lm.getString("choice.no")),
                List.of(saveAndExit, confirmExitNoSave),
                () -> changeState(GameState.WORLD)
        );

        ((ChoiceState) gameStates.get(GameState.CHOICE_BOX)).configure(context);
        changeState(GameState.CHOICE_BOX);
    }

    // Utility
    public void displayOnScreenMessage(String message) {
        messageManager.showMessage(message);
    }

    public InputHandler getInput() { return input; }
    public long getLastMenuToggleTime() { return lastMenuToggleTime; }
    public long getMenuToggleCooldown() { return menuToogleCooldown; }
    public Player getPlayer() { return player; }
    public void setInBattle(boolean b) { inBattle = b; }
    public void resetInput() { input.reset(); }
}
