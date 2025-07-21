package render;

import entities.Player;
import world.WorldMap;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Responsabile del disegno della mappa e del personaggio sullo schermo.
 */
public class WorldRenderer {

    private BufferedImage[][] tiles;
    private int tileSize;
    private int scale;
    private int panelWidth, panelHeight;

    public void init(BufferedImage[][] tiles, int tileSize, int scale, int panelWidth, int panelHeight) {
        this.tiles = tiles;
        this.tileSize = tileSize;
        this.scale = scale;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    public void drawWorld(Graphics2D g, WorldMap map, Player player) {
        if (tiles == null) return;

        int cameraX = player.x * tileSize * scale - (panelWidth / 2) + (tileSize * scale / 2);
        int cameraY = player.y * tileSize * scale - (panelHeight / 2) + (tileSize * scale / 2);

        g.translate(-cameraX, -cameraY);

        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                drawTile(g, map.getGroundTile(x, y), x, y);
                drawTile(g, map.getOverlayTile(x, y), x, y);
            }
        }

        player.draw(g, scale);

        g.translate(cameraX, cameraY);
    }

    private void drawTile(Graphics2D g, int tileId, int x, int y) {
        if (tileId <= 0) return;

        int id = tileId - 1;
        int tileX = id % tiles[0].length;
        int tileY = id / tiles[0].length;

        g.drawImage(
                tiles[tileY][tileX],
                x * tileSize * scale,
                y * tileSize * scale,
                tileSize * scale,
                tileSize * scale,
                null
        );
    }
}
