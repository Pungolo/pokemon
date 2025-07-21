package render;

import entities.Player;
import world.WorldMap;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WorldRenderer {

    private final int tileSize;
    private final int scale;
    private final BufferedImage[][] tiles;

    public WorldRenderer(BufferedImage[][] tiles, int tileSize, int scale) {
        this.tiles = tiles;
        this.tileSize = tileSize;
        this.scale = scale;
    }

    public void render(Graphics2D g, WorldMap map, Player player, int panelWidth, int panelHeight) {
        int cameraX = player.x * tileSize * scale - panelWidth / 2 + tileSize;
        int cameraY = player.y * tileSize * scale - panelHeight / 2 + tileSize;

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
        if (tileId > 0) {
            int id = tileId - 1;
            int tileX = id % tiles[0].length;
            int tileY = id / tiles[0].length;
            g.drawImage(tiles[tileY][tileX], x * tileSize * scale, y * tileSize * scale, tileSize * scale, tileSize * scale, null);
        }
    }
}
