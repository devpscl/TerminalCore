package net.pascal.terminal.util;

public enum MoveDirection {

    UP(1),
    DOWN(2),
    LEFT(3),
    RIGHT(4);

    private int i;

    MoveDirection(int i) {
        this.i = i;
    }

    public int getId() {
        return i;
    }
}
