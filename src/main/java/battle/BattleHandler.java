package battle;

import entities.Player;
import entities.Pokemon;
import main.GamePanel;
import main.GameWindow;

/**
 * Gestisce l'avvio e la chiusura delle battaglie, lo stato globale inBattle,
 * la visualizzazione della schermata di battaglia, e il salvataggio automatico.
 */
public class BattleHandler {

    private final GameWindow parent;
    private final GamePanel parentPanel;
    private final Player player;

    private boolean inBattle = false;
    private BattleEndListener battleEndListener;

    public BattleHandler(GameWindow parent, GamePanel parentPanel, Player player) {
        this.parent = parent;
        this.parentPanel = parentPanel;
        this.player = player;
    }

    public boolean isInBattle() {
        return inBattle;
    }

    public void setBattleEndListener(BattleEndListener listener) {
        this.battleEndListener = listener;
    }

    public void startBattle(Pokemon playerPokemon, Pokemon enemyPokemon) {
        if (inBattle) return;

        if (playerPokemon == null || enemyPokemon == null) {
            throw new IllegalArgumentException("I Pokémon non possono essere null!");
        }

        System.out.printf("Inizio battaglia: %s (player) vs %s (enemy)%n",
                playerPokemon.getName(), enemyPokemon.getName());

        inBattle = true;

        BattleScreen battleScreen = new BattleScreen(playerPokemon, enemyPokemon, parent, parentPanel, player);

        // Quando la battaglia termina, torniamo al GamePanel e notifichiamo il listener
        battleScreen.setOnBattleEnd(result -> {
            inBattle = false;
            player.getParty().saveToFile(); // Salvataggio automatico
            parentPanel.setInBattle(false);
            parent.setContentPane(parentPanel);
            parent.revalidate();
            parent.repaint();
            parentPanel.startGameLoop();

            if (battleEndListener != null) {
                battleEndListener.onBattleEnd(result);
            }
        });

        parent.setContentPane(battleScreen);
        parent.revalidate();
        parent.repaint();
    }
}
