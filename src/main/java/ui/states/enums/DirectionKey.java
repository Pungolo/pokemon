package ui.states.enums;

import entities.Player;

import java.awt.event.KeyEvent;

public enum DirectionKey {
    UP(KeyEvent.VK_UP, 0, -1, Player.Direction.UP, null),
    DOWN(KeyEvent.VK_DOWN, 0, 1, Player.Direction.DOWN, null),
    LEFT(KeyEvent.VK_LEFT, -1, 0, Player.Direction.SIDE, true),
    RIGHT(KeyEvent.VK_RIGHT, 1, 0, Player.Direction.SIDE, false);

    public final int key;
    public final int dx, dy;
    public final Player.Direction dir;
    public final Boolean side;

    DirectionKey(int key, int dx, int dy, Player.Direction dir, Boolean side) {
        this.key = key; this.dx = dx; this.dy = dy;
        this.dir = dir; this.side = side;
    }
}

