package battle;

import entities.Player;
import entities.Pokemon;
import main.GamePanel;
import main.GameWindow;

/**
 * Responsabile dell'avvio della schermata di battaglia e della gestione dello stato inBattle.
 */
public class BattleHandler {

    private final GameWindow window;
    private final GamePanel panel;
    private final Player player;

    public BattleHandler(GameWindow window, GamePanel panel, Player player) {
        this.window = window;
        this.panel = panel;
        this.player = player;
    }

    public void startBattle(Pokemon playerPokemon, Pokemon enemyPokemon) {
        if (playerPokemon == null || enemyPokemon == null) {
            throw new IllegalArgumentException("I Pokémon non possono essere null.");
        }

        System.out.printf("Inizio battaglia: %s (giocatore) vs %s (selvatico)%n",
                playerPokemon.getName(), enemyPokemon.getName());

        BattleScreen battleScreen = new BattleScreen(playerPokemon, enemyPokemon, window, panel, player);
        battleScreen.setOnBattleEnd(result -> {
            panel.setInBattle(false);
            System.out.println("Battaglia terminata. Risultato: " + result);
        });

        window.setContentPane(battleScreen);
        window.revalidate();
        window.repaint();
    }
}
